package com.swp391.evdealersystem.dto.request;

import com.swp391.evdealersystem.enums.VehicleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ElectricVehicleRequest {
    private Long modelId;
    private String modelCode;

    @NotNull(message = "Cost is required")
    private BigDecimal cost;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotNull(message = "Battery capacity is required")
    private Integer batteryCapacity;

    private String imageUrl;
    private VehicleStatus status;
}
