package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.PaymentRequest;
import com.swp391.evdealersystem.dto.response.PaymentResponse;
import com.swp391.evdealersystem.entity.Payment;
import com.swp391.evdealersystem.enums.PaymentMethod;
import com.swp391.evdealersystem.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class PaymentMapper {
    public Payment toEntity(PaymentRequest req, String vnpTxnRef, String payUrl) {
        return Payment.builder()
                .amount(req.getAmount())
                .paymentMethod(PaymentMethod.VNPAY)
                .status(PaymentStatus.PENDING)
                .vnpTxnRef(vnpTxnRef)
                .payUrl(payUrl)
                .bankCode(req.getBankCode())
                .orderId(req.getOrderId())
                .createdAt(OffsetDateTime.now())
                .build();
    }

    public PaymentResponse toDTO(Payment p) {
        if (p == null) return null;
        return PaymentResponse.builder()
                .paymentId(p.getPaymentId())
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .status(p.getStatus())
                .vnpTxnRef(p.getVnpTxnRef())
                .vnpTransactionNo(p.getVnpTransactionNo())
                .vnpResponseCode(p.getVnpResponseCode())
                .vnpTransactionStatus(p.getVnpTransactionStatus())
                .bankCode(p.getBankCode())
                .bankTranNo(p.getBankTranNo())
                .payUrl(p.getPayUrl())
                .orderId(p.getOrderId())
                .createdAt(p.getCreatedAt())
                .paidAt(p.getPaidAt())
                .build();
    }
}