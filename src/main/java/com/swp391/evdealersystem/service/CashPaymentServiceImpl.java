package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.CashPaymentRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;
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

@Service
@RequiredArgsConstructor
public class CashPaymentServiceImpl implements CashPaymentService {

    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;
    private final VehicleSerialRepository serialRepo;
    private final WarehouseStockRepository stockRepo;
    private final WarehouseRepository warehouseRepo;
    private final OrderMapper mapper;

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
                .status(PaymentStatus.PAID)      // bạn đang dùng enum này
                .type(applyTo)                   // PaymentPurpose.DEPOSIT/REMAINING
                .method(PaymentMethod.CASH)
                .paymentDate(LocalDateTime.now())
                .message(req.getNote())
                .build();
        paymentRepo.save(pay);

        // Cập nhật số tiền đã TRẢ (depositAmount)
        BigDecimal newDeposit = deposit.add(paid);
        if (newDeposit.compareTo(price) > 0) newDeposit = price; // chặn overpay
        order.setDepositAmount(newDeposit);

        // Tính trạng thái theo NGHIỆP VỤ:
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
}