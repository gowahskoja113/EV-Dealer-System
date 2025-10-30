package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ManualPayRequest;
import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.enums.OrderPaymentStatus;
import com.swp391.evdealersystem.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepo;

    @Transactional
    @Override
    public void manualPay(Long orderId, ManualPayRequest req) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        BigDecimal amount = req.getAmount();
        if (amount == null || amount.signum() <= 0) return; // không xử lý nếu không hợp lệ

        BigDecimal currentDeposit = nvl(order.getDepositAmount());
        BigDecimal newDeposit = currentDeposit.add(amount);
        order.setDepositAmount(newDeposit);

        // Cập nhật trạng thái thanh toán dựa trên depositAmount (đóng vai trò "đã trả")
        BigDecimal total = nvl(order.getTotalAmount());
        int cmp = newDeposit.compareTo(total);
        if (cmp == 0) {
            order.setPaymentStatus(OrderPaymentStatus.PAID);
        } else if (cmp > 0) {
            order.setPaymentStatus(OrderPaymentStatus.OVERPAID);
        } else if (newDeposit.signum() > 0) {
            order.setPaymentStatus(OrderPaymentStatus.PARTIALLY_PAID);
        } else {
            order.setPaymentStatus(OrderPaymentStatus.UNPAID);
        }

        orderRepo.save(order);

        // (tùy chọn) ghi log lịch sử thanh toán nếu có bảng Payment riêng
    }

    private static BigDecimal nvl(BigDecimal v) {
        return Objects.requireNonNullElse(v, BigDecimal.ZERO);
    }
}
