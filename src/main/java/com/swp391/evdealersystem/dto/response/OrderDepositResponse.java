package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.OrderPaymentStatus;
import com.swp391.evdealersystem.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class OrderDepositResponse {
    private Long orderId;
    private Long customerId;
    private Long vehicleId;

    private BigDecimal depositAmount;
    private BigDecimal remainingAmount;

    private OrderPaymentStatus paymentStatus;
    private OrderStatus status;

    private String currency;
    private LocalDateTime orderDate;
    private LocalDate deliveryDate;
}