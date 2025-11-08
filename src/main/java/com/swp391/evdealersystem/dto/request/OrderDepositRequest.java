package com.swp391.evdealersystem.dto.request;

import com.swp391.evdealersystem.enums.OrderPaymentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDepositRequest {

    @NotNull
    private Long customerId;

    @NotBlank(message = "VIN cannot be blank")
    private String vin;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal depositAmount;

    /**
     * userId của sales (lấy từ assignedSales trong Customer).
     * Bạn có thể truyền hoặc bỏ qua. Nếu bỏ qua, service auto lấy từ Customer.assignedSales.
     * Hiện chưa lưu vào Order (vì entity Order chưa có field sales), dùng để kiểm tra/ghi log.
     */
    private Long userId;

    /** Nếu null -> service dùng LocalDateTime.now() */
    private LocalDateTime orderDate;
} 