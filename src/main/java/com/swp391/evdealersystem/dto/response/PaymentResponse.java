package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.PaymentMethod;
import com.swp391.evdealersystem.enums.PaymentPurpose;
import com.swp391.evdealersystem.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;
    private PaymentPurpose purpose;     // DEPOSIT | BALANCE
    private PaymentMethod method;       // VNPAY | CASH
    private PaymentStatus status;       // PENDING | PAID | FAILED | CANCELED
    private String vnpTxnRef;
    private String payUrl;              // chỉ có khi VNPAY PENDING
    private OffsetDateTime createdAt;
    private OffsetDateTime paidAt;
}
