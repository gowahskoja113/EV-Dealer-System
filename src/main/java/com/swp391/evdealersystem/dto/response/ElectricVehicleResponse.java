package com.swp391.evdealersystem.dto.response;

import lombok.Data;

@Data
public class ElectricVehicleResponse {
    private Long vehicleId;
    private Long cost;
    private Long price;
    private Integer batteryCapacity;

    private Long modelId;
    private String modelCode;
    private String brand;
    private String color;
    private Integer productionYear;
}
