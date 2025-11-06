package com.swp391.evdealersystem.dto.request;

import com.swp391.evdealersystem.enums.PaymentPurpose;
import jakarta.validation.constraints.NotNull;

public record StartVnpayRequest(
        @NotNull PaymentPurpose purpose, // DEPOSIT hoặc REMAINING
        String bankCode                  // optional
) {}