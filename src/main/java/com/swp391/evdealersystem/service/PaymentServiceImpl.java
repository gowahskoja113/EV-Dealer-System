package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.CashPaymentRequest;
import com.swp391.evdealersystem.dto.request.StartVnpayRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.dto.response.PaymentHistoryResponse;
import com.swp391.evdealersystem.dto.response.StartVnpayResponse;
import com.swp391.evdealersystem.dto.response.VnpIpnResponse;
import com.swp391.evdealersystem.entity.*;
import com.swp391.evdealersystem.enums.*;
import com.swp391.evdealersystem.mapper.OrderMapper;
import com.swp391.evdealersystem.repository.*;
import com.swp391.evdealersystem.util.AuthenticationHelper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final CustomerRepository customerRepo;
    private final AuthenticationHelper authenticationHelper;

    private void validatePaymentAccess(Order order) {
        User currentUser = authenticationHelper.getCurrentUser();
        if ("ROLE_ADMIN".equalsIgnoreCase(currentUser.getRole().getRoleName())) return;

        if (currentUser.getDealership() == null) {
            throw new AccessDeniedException("Nhân viên chưa được gán vào Đại lý.");
        }

        VehicleSerial serial = order.getSerial();
        if (serial == null || serial.getWarehouse() == null || serial.getWarehouse().getDealership() == null) {
            return;
        }

        Long userDealerId = currentUser.getDealership().getDealershipId();
        Long orderDealerId = serial.getWarehouse().getDealership().getDealershipId();

        if (!userDealerId.equals(orderDealerId)) {
            throw new AccessDeniedException("CHẶN: Bạn không có quyền thu tiền cho đơn hàng của Đại lý khác.");
        }
    }

    private void handleFullyPaid(Order order, VehicleSerial serial) {
        order.setPaymentStatus(OrderPaymentStatus.PAID);
        order.setStatus(OrderStatus.ORDER_PAID);

        if (order.getFullyPaidAt() == null) order.setFullyPaidAt(LocalDateTime.now());
        if (order.getDepositPaidAt() == null) order.setDepositPaidAt(LocalDateTime.now());

        Customer customer = order.getCustomer();
        if (customer != null && customer.getStatus() == CustomerStatus.LEAD) {
            customer.setStatus(CustomerStatus.CUSTOMER);
            customerRepo.save(customer);
        }

        if (serial.getStatus() != VehicleStatus.UNDELIVERED && serial.getStatus() != VehicleStatus.SOLD_OUT) {

            serial.setStatus(VehicleStatus.UNDELIVERED);
            serial.setHoldUntil(null);
            serialRepo.save(serial);

            Long warehouseId = serial.getWarehouse().getWarehouseId();
            Long modelId = serial.getModel().getModelId();

            var stock = stockRepo.findForUpdate(warehouseId, modelId)
                    .orElseThrow(() -> new IllegalStateException("Stock not found"));

            if (stock.getQuantity() <= 0) {
                throw new IllegalStateException("Kho bị âm, không thể trừ xe.");
            }

            stock.setQuantity(stock.getQuantity() - 1);
            stockRepo.save(stock);

            int total = stockRepo.sumQuantityByWarehouseId(warehouseId);
            var wh = serial.getWarehouse();
            wh.setVehicleQuantity(total);
            warehouseRepo.save(wh);
        }
    }

    @Transactional
    @Override
    public OrderResponse processCash(Long orderId, CashPaymentRequest req) {
        Order order = orderRepo.findGraphByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        validatePaymentAccess(order);

        if (order.getPaymentStatus() == OrderPaymentStatus.PAID) {
            return mapper.toOrderResponse(order);
        }

        VehicleSerial serial = order.getSerial();
        if (serial == null || serial.getVehicle() == null) throw new IllegalStateException("Order missing vehicle.");

        BigDecimal price = serial.getVehicle().getPrice() != null ? serial.getVehicle().getPrice() : BigDecimal.ZERO;
        BigDecimal deposit = order.getDepositAmount() != null ? order.getDepositAmount() : BigDecimal.ZERO;
        BigDecimal paid = req.getAmount();

        // Save Payment
        Payment pay = Payment.builder()
                .order(order).amount(paid).status(PaymentStatus.PAID)
                .type((req.getApplyTo() != null) ? req.getApplyTo() : PaymentPurpose.DEPOSIT)
                .method(PaymentMethod.CASH).paymentDate(LocalDateTime.now()).message(req.getNote())
                .build();
        paymentRepo.save(pay);

        BigDecimal newDeposit = deposit.add(paid);
        if (newDeposit.compareTo(price) > 0) newDeposit = price;
        order.setDepositAmount(newDeposit);

        boolean fullyPaid = newDeposit.compareTo(price) >= 0;
        BigDecimal planned = order.getPlannedDepositAmount() == null ? BigDecimal.ZERO : order.getPlannedDepositAmount();

        if (fullyPaid) {
            handleFullyPaid(order, serial);
        } else {
            // Thanh toán 1 phần (Cọc)
            if (planned.signum() > 0 && newDeposit.compareTo(planned) >= 0) {
                order.setPaymentStatus(OrderPaymentStatus.DEPOSIT_PAID);
                if (order.getDepositPaidAt() == null) order.setDepositPaidAt(LocalDateTime.now());

                // --- [BỔ SUNG MỚI] GIA HẠN HOLD 14 NGÀY KỂ TỪ LÚC TRẢ CỌC ---
                if (serial.getStatus() != VehicleStatus.SOLD_OUT && serial.getStatus() != VehicleStatus.UNDELIVERED) {
                    // Đảm bảo trạng thái là HOLD
                    serial.setStatus(VehicleStatus.HOLD);
                    // Update thời gian: Now + 14 ngày
                    serial.setHoldUntil(LocalDateTime.now().plusDays(14).atOffset(java.time.ZoneOffset.UTC));
                    serialRepo.save(serial);
                }

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

        validatePaymentAccess(order);

        var price = order.getSerial().getVehicle().getPrice();
        var deposit = order.getDepositAmount() == null ? BigDecimal.ZERO : order.getDepositAmount();
        BigDecimal toPay;

        if (req.purpose() == PaymentPurpose.DEPOSIT) {
            var planned = order.getPlannedDepositAmount();
            if (planned == null) throw new IllegalStateException("No planned deposit");
            toPay = planned.subtract(deposit);
        } else {
            toPay = price.subtract(deposit);
        }

        if (toPay.signum() <= 0) throw new IllegalStateException("Already paid enough");

        Payment p = Payment.builder().order(order).amount(toPay).status(PaymentStatus.PENDING)
                .type(req.purpose()).method(PaymentMethod.VNPAY).build();
        p = paymentRepo.save(p);
        p.setTransactionRef(String.valueOf(p.getId()));
        paymentRepo.save(p);

        String url = vnpayService.createPaymentUrl(toPay.longValueExact(), req.bankCode(), order.getOrderId(), p.getId(), req.purpose().name());
        return new StartVnpayResponse(p.getId(), url);
    }

    @Override
    @Transactional
    public VnpIpnResponse processVnpayCallback(Map<String, String> params) {
        if (!vnpayService.verifySignature(params)) return VnpIpnResponse.fail("97", "Invalid signature");

        String rsp = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");
        long amount = Long.parseLong(params.get("vnp_Amount"));

        var payment = paymentRepo.findByTransactionRef(txnRef)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));

        if (payment.getStatus() == PaymentStatus.PAID) return VnpIpnResponse.ok("00", "Already processed");

        long expected = payment.getAmount().longValueExact() * 100L;
        if (expected != amount) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepo.save(payment);
            return VnpIpnResponse.fail("04", "Amount mismatch");
        }

        if ("00".equals(rsp)) {
            payment.setStatus(PaymentStatus.PAID);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepo.save(payment);

            var order = payment.getOrder();
            var serial = order.getSerial();

            if (payment.getType() == PaymentPurpose.DEPOSIT) {
                var newDeposit = (order.getDepositAmount() == null ? BigDecimal.ZERO : order.getDepositAmount()).add(payment.getAmount());
                order.setDepositAmount(newDeposit);

                var planned = order.getPlannedDepositAmount();
                if (planned != null && newDeposit.compareTo(planned) >= 0) {
                    order.setPaymentStatus(OrderPaymentStatus.DEPOSIT_PAID);
                    if(order.getDepositPaidAt() == null) order.setDepositPaidAt(LocalDateTime.now());

                    // --- [BỔ SUNG MỚI] GIA HẠN HOLD 14 NGÀY ---
                    if (serial.getStatus() != VehicleStatus.SOLD_OUT && serial.getStatus() != VehicleStatus.UNDELIVERED) {
                        serial.setStatus(VehicleStatus.HOLD);
                        serial.setHoldUntil(LocalDateTime.now().plusDays(14).atOffset(java.time.ZoneOffset.UTC));
                        serialRepo.save(serial);
                    }
                    // ------------------------------------------
                }
                orderRepo.save(order);
            } else if (payment.getType() == PaymentPurpose.REMAINING) {
                if (serial != null && serial.getVehicle() != null) {
                    order.setDepositAmount(serial.getVehicle().getPrice());
                }
                handleFullyPaid(order, serial);
                orderRepo.save(order);
            }
            return VnpIpnResponse.ok("00", "Success");
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepo.save(payment);
            return VnpIpnResponse.fail("24", "Failed");
        }
    }
    @Override
    @Transactional(readOnly = true)
    public Page<PaymentHistoryResponse> getPayments(Long customerId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        User currentUser = authenticationHelper.getCurrentUser();
        Long dealershipId = null;

        if (!"ROLE_ADMIN".equalsIgnoreCase(currentUser.getRole().getRoleName())) {
            if (currentUser.getDealership() == null) {
                throw new AccessDeniedException("Nhân viên không thuộc đại lý nào.");
            }
            dealershipId = currentUser.getDealership().getDealershipId();
        }

        // Xử lý ngày tháng (chuyển LocalDate sang LocalDateTime đầu ngày và cuối ngày)
        LocalDateTime fromDateTime = (fromDate != null) ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = (toDate != null) ? toDate.atTime(23, 59, 59) : null;

        Page<Payment> payments = paymentRepo.searchPayments(dealershipId, customerId, fromDateTime, toDateTime, pageable);

        // Map Entity sang DTO
        return payments.map(p -> PaymentHistoryResponse.builder()
                .paymentId(p.getId())
                .orderId(p.getOrder().getOrderId())
                .customerName(p.getOrder().getCustomer() != null ? p.getOrder().getCustomer().getName() : "N/A")
                .customerId(p.getOrder().getCustomer() != null ? p.getOrder().getCustomer().getCustomerId() : null)
                .amount(p.getAmount())
                .status(p.getStatus())
                .type(p.getType())
                .method(p.getMethod())
                .transactionRef(p.getTransactionRef())
                .paymentDate(p.getPaymentDate())
                .message(p.getMessage())
                .build());
    }
}