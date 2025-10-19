package com.swp391.evdealersystem.dto.request;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PaymentRequest {
    @NotNull @DecimalMin(value = "0.01")
    private BigDecimal amount; // VND


    @Size(max = 64)
    private String orderId; // đơn hàng trong hệ thống của bạn


    @Size(max = 20)
    private String bankCode; // optional, ví dụ: NCB, VNPAYQR...
}