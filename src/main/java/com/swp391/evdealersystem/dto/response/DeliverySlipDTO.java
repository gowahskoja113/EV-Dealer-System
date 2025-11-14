package com.swp391.evdealersystem.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliverySlipDTO {
    private Long orderId;
    private LocalDate deliveryDate;
    private String salespersonName;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private String vehicleBrand;
    private String vehicleModelCode;
    private String vehicleColor;
    private String vehicleVin;

    private BigDecimal vehiclePrice;
    private BigDecimal amountPaid;
    private BigDecimal remainingAmount;
}