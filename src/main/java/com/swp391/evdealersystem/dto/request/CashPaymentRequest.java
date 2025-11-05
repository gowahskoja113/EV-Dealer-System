package com.swp391.evdealersystem.dto.request;

import com.swp391.evdealersystem.enums.PaymentPurpose;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashPaymentRequest {
    @NotNull @DecimalMin("0.01")
    private BigDecimal amount;

    /** "DEPOSIT" (default) hoặc "REMAINING" */
    private PaymentPurpose applyTo;

    private String note;
}
