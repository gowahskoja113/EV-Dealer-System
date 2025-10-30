package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.response.PaymentResponse;
import com.swp391.evdealersystem.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getPaymentId())
                .orderId(p.getOrder() != null ? p.getOrder().getOrderId() : null)
                .amount(p.getAmount())
                .purpose(p.getPurpose())
                .method(p.getPaymentMethod())
                .status(p.getStatus())
                .vnpTxnRef(p.getVnpTxnRef())
                .payUrl(p.getPayUrl())
                .createdAt(p.getCreatedAt())
                .paidAt(p.getPaidAt())
                .build();
    }
}
