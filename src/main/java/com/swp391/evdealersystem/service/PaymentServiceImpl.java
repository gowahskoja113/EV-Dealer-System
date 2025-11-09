package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.CashPaymentRequest;
import com.swp391.evdealersystem.dto.request.StartVnpayRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.dto.response.StartVnpayResponse;
import com.swp391.evdealersystem.dto.response.VnpIpnResponse;
import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.entity.Payment;
import com.swp391.evdealersystem.entity.VehicleSerial;
import com.swp391.evdealersystem.enums.*;
import com.swp391.evdealersystem.mapper.OrderMapper;
import com.swp391.evdealersystem.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;
    private final VehicleSerialRepository serialRepo;
    private final WarehouseStockRepository stockRepo;
    private final WarehouseRepository warehouseRepo;
    private final OrderMapper mapper;
    private final VNPAYService vnpayService;

    @Transactional
    public OrderResponse processCash(Long orderId, CashPaymentRequest req) {
        Order order = orderRepo.findGraphByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        // Idempotent: đã thanh toán đủ rồi thì trả về luôn
        if (order.getPaymentStatus() == OrderPaymentStatus.PAID) {
            return mapper.toOrderResponse(order);
        }

        VehicleSerial serial = order.getSerial();
        if (serial == null || serial.getVehicle() == null) {
            throw new IllegalStateException("Order missing vehicle/serial.");
        }

        BigDecimal price   = serial.getVehicle().getPrice() != null ? serial.getVehicle().getPrice() : BigDecimal.ZERO;
        BigDecimal deposit = order.getDepositAmount() != null ? order.getDepositAmount() : BigDecimal.ZERO;
        BigDecimal paid    = req.getAmount();
        if (paid == null || paid.signum() <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }

        // Mặc định coi là DEPOSIT nếu không truyền
        PaymentPurpose applyTo = (req.getApplyTo() != null) ? req.getApplyTo() : PaymentPurpose.DEPOSIT;

        // Ghi sổ thanh toán tiền mặt (thành công ngay)
        Payment pay = Payment.builder()
                .order(order)
                .amount(paid)
                .status(PaymentStatus.PAID)
                .type(applyTo)
                .method(PaymentMethod.CASH)
                .paymentDate(LocalDateTime.now())
                .message(req.getNote())
                .build();
        paymentRepo.save(pay);

        // Cập nhật số tiền đã TRẢ (depositAmount)
        BigDecimal newDeposit = deposit.add(paid);
        if (newDeposit.compareTo(price) > 0) newDeposit = price;
        order.setDepositAmount(newDeposit);

        boolean fullyPaid = newDeposit.compareTo(price) >= 0;
        BigDecimal planned = order.getPlannedDepositAmount() == null ? BigDecimal.ZERO : order.getPlannedDepositAmount();

        if (fullyPaid) {
            // đủ tiền → PAID/COMPLETED + SOLD_OUT + trừ kho
            order.setPaymentStatus(OrderPaymentStatus.PAID);
            order.setStatus(OrderStatus.COMPLETED);

            if (serial.getStatus() != VehicleStatus.SOLD_OUT) {
                serial.setStatus(VehicleStatus.SOLD_OUT);
                serial.setHoldUntil(null);
                serialRepo.save(serial);

                Long whId = serial.getWarehouse().getWarehouseId();
                Long modelId = serial.getModel().getModelId();

                var stock = stockRepo.findForUpdate(whId, modelId)
                        .orElseThrow(() -> new IllegalStateException("Stock not found for warehouse/model"));
                if (stock.getQuantity() <= 0) {
                    throw new IllegalStateException("Stock would go negative for model " + serial.getModel().getModelCode());
                }
                stock.setQuantity(stock.getQuantity() - 1);
                stockRepo.save(stock);

                int total = stockRepo.sumQuantityByWarehouseId(whId);
                var wh = serial.getWarehouse();
                wh.setVehicleQuantity(total);
                warehouseRepo.save(wh);
            }
        } else {
            if (planned.signum() > 0 && newDeposit.compareTo(planned) >= 0) {
                order.setPaymentStatus(OrderPaymentStatus.DEPOSIT_PAID);
            } else {
                order.setPaymentStatus(OrderPaymentStatus.UNPAID);
            }
            order.setStatus(OrderStatus.PROCESSING);
        }

        order = orderRepo.save(order);
        return mapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public StartVnpayResponse startVnpay(Long orderId, StartVnpayRequest req) {
        Order order = orderRepo.findGraphByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        if (order.getSerial() == null || order.getSerial().getVehicle() == null
                || order.getSerial().getVehicle().getPrice() == null) {
            throw new IllegalStateException("Order missing vehicle/price.");
        }

        var price   = order.getSerial().getVehicle().getPrice();
        var deposit = order.getDepositAmount() == null ? java.math.BigDecimal.ZERO : order.getDepositAmount();

        java.math.BigDecimal toPay;
        if (req.purpose() == PaymentPurpose.DEPOSIT) {
            var planned = order.getPlannedDepositAmount();
            if (planned == null || planned.signum() <= 0) {
                throw new IllegalStateException("No planned deposit on order.");
            }
            // nếu đã cọc 1 phần, chỉ thu phần còn lại của kế hoạch
            toPay = planned.subtract(deposit);
            if (toPay.signum() <= 0) {
                throw new IllegalStateException("Planned deposit already satisfied.");
            }
        } else {
            toPay = price.subtract(deposit);
            if (toPay.signum() <= 0) {
                throw new IllegalStateException("Order is already fully paid.");
            }
        }

        // tạo Payment PENDING
        Payment p = Payment.builder()
                .order(order)
                .amount(toPay)
                .status(PaymentStatus.PENDING)
                .type(req.purpose())
                .method(PaymentMethod.VNPAY)
                .build();
        p = paymentRepo.save(p);
        p.setTransactionRef(String.valueOf(p.getId()));
        paymentRepo.save(p);

        String url = vnpayService.createPaymentUrl(
                toPay.longValueExact(),
                req.bankCode(),
                order.getOrderId(),
                p.getId(),
                req.purpose().name()
        );

        return new StartVnpayResponse(p.getId(), url);
    }

    @Override
    @Transactional
    public VnpIpnResponse processVnpayCallback(Map<String, String> params) {
        if (!vnpayService.verifySignature(params)) {
            return VnpIpnResponse.fail("97", "Invalid signature");
        }

        String rsp = params.get("vnp_ResponseCode"); // "00" success
        String txnRef = params.get("vnp_TxnRef");    // = paymentId
        String transNo = params.get("vnp_TransactionNo");
        String payDate = params.get("vnp_PayDate");
        long amount = Long.parseLong(params.get("vnp_Amount")); // x100

        var payment = paymentRepo.findByTransactionRef(txnRef)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for vnp_TxnRef=" + txnRef));

        if (payment.getStatus() == PaymentStatus.PAID) {
            return VnpIpnResponse.ok("00", "Already processed");
        }

        long expected = payment.getAmount().longValueExact() * 100L;
        if (expected != amount) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setMessage("Amount mismatch: expected=" + expected + ", actual=" + amount);
            paymentRepo.save(payment);
            return VnpIpnResponse.fail("04", "Invalid amount");
        }

        if ("00".equals(rsp)) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setMessage("VNPay transNo=" + transNo + " payDate=" + payDate);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepo.save(payment);

            // cập nhật Order tương tự phần mình đã hướng dẫn (DEPOSIT/REMAINING)
            var order = payment.getOrder();
            var serial = order.getSerial();

            if (payment.getType() == PaymentPurpose.DEPOSIT) {
                var newDeposit = (order.getDepositAmount() == null ? java.math.BigDecimal.ZERO : order.getDepositAmount())
                        .add(payment.getAmount());
                order.setDepositAmount(newDeposit);

                var planned = order.getPlannedDepositAmount() == null ? java.math.BigDecimal.ZERO : order.getPlannedDepositAmount();
                if (planned.signum() > 0 && newDeposit.compareTo(planned) >= 0) {
                    order.setPaymentStatus(OrderPaymentStatus.DEPOSIT_PAID);
                } else {
                    order.setPaymentStatus(OrderPaymentStatus.UNPAID); // hoặc PARTIAL nếu bạn có enum
                }
                order.setStatus(com.swp391.evdealersystem.enums.OrderStatus.PROCESSING);
                orderRepo.save(order);

            } else if (payment.getType() == PaymentPurpose.REMAINING) {
                order.setPaymentStatus(OrderPaymentStatus.PAID);
                order.setStatus(com.swp391.evdealersystem.enums.OrderStatus.COMPLETED);

                if (serial != null && serial.getStatus() != VehicleStatus.SOLD_OUT) {
                    serial.setStatus(VehicleStatus.SOLD_OUT);
                    serial.setHoldUntil(null);
                    serialRepo.save(serial);

                    Long whId = serial.getWarehouse().getWarehouseId();
                    Long modelId = serial.getModel().getModelId();

                    var stock = stockRepo.findForUpdate(whId, modelId)
                            .orElseThrow(() -> new IllegalStateException("Stock not found for warehouse/model"));
                    if (stock.getQuantity() <= 0) {
                        throw new IllegalStateException("Stock would go negative for model " + serial.getModel().getModelCode());
                    }
                    stock.setQuantity(stock.getQuantity() - 1);
                    stockRepo.save(stock);

                    int total = stockRepo.sumQuantityByWarehouseId(whId);
                    var wh = serial.getWarehouse();
                    wh.setVehicleQuantity(total);
                    warehouseRepo.save(wh);
                }

                if (serial != null && serial.getVehicle() != null && serial.getVehicle().getPrice() != null) {
                    order.setDepositAmount(serial.getVehicle().getPrice());
                }
                orderRepo.save(order);
            }

            return VnpIpnResponse.ok("00", "Success");
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setMessage("VNPay failed code=" + rsp);
            paymentRepo.save(payment);
            return VnpIpnResponse.fail("24", "Payment failed: " + rsp);
        }
    }
}