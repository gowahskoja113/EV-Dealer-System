package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.PaymentMethod;
import com.swp391.evdealersystem.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentResponse {
    private Long paymentId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;

    private String vnpTxnRef;
    private String vnpTransactionNo;
    private String vnpResponseCode;
    private String vnpTransactionStatus;

    private String bankCode;
    private String bankTranNo;
    private String payUrl;

    private String orderId;
    private OffsetDateTime createdAt;
    private OffsetDateTime paidAt;
}