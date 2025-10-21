package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private Long orderId;

    private Long customerId;
    private String customerName;
    private String customerPhone;

    private Long vehicleId;
    private Long vehicleModelId;
    private String vehicleModelCode;

    private LocalDateTime orderDate;
    private OrderStatus status;
}