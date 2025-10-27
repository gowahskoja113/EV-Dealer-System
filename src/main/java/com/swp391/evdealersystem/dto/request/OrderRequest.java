package com.swp391.evdealersystem.dto.request;

import com.swp391.evdealersystem.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OrderRequest {
    private Long customerId;
    private Long vehicleId;
    private BigDecimal totalAmount;
    private BigDecimal depositAmount;
    private OrderStatus status;
    private LocalDate deliveryDate;
}
