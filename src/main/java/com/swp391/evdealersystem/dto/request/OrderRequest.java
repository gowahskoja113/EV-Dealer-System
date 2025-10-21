package com.swp391.evdealersystem.dto.request;

import com.swp391.evdealersystem.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long vehicleId;

    private LocalDateTime orderDate;

    @NotNull
    private OrderStatus status;
}