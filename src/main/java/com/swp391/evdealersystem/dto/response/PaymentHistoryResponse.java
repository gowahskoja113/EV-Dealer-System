package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.PaymentMethod;
import com.swp391.evdealersystem.enums.PaymentPurpose;
import com.swp391.evdealersystem.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentHistoryResponse {
    private Long paymentId;
    private Long orderId;
    private String customerName;
    private Long customerId;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentPurpose type;
    private PaymentMethod method;
    private String transactionRef;
    private LocalDateTime paymentDate;
    private String message;
}