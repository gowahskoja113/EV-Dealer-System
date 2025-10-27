package com.swp391.evdealersystem.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ElectricVehicleResponse {
    private Long vehicleId;
    private BigDecimal cost;
    private BigDecimal price;
    private Integer batteryCapacity;

    private Long modelId;
    private String modelCode;
    private String brand;
    private String color;
    private Integer productionYear;
}
