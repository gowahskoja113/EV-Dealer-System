package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.OrderPaymentStatus;
import com.swp391.evdealersystem.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DepositOrderView {
    private Long orderId;
    private LocalDateTime orderDate;

    private Long customerId;
    private String customerName;

    private String vin;
    private Long vehicleId;
    private String vehicleModel; // ví dụ lấy brand/tên model tuỳ schema của bạn
    private BigDecimal price;    // giá xe

    private BigDecimal depositAmount;
    private BigDecimal remainingAmount;
    private OrderPaymentStatus paymentStatus;
    private OrderStatus status;
    private String currency;
    private LocalDate deliveryDate;
}