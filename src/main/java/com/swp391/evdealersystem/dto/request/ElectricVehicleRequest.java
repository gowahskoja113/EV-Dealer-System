package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ElectricVehicleRequest {

    @NotNull
    private String modelCode;

    @NotNull
    private BigDecimal cost;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer batteryCapacity;
}
