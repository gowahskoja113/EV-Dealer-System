package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private String customerName;
    private Long vehicleId;
    private String vehicleModel;
    private BigDecimal totalAmount;
    private BigDecimal depositAmount;
    private OrderStatus status;
    private OrderPaymentStatus paymentStatus;
    private LocalDate deliveryDate;
    private LocalDateTime orderDate;
}
