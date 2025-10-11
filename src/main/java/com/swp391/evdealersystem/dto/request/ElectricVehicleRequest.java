package com.swp391.evdealersystem.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ElectricVehicleRequest {
    private Integer modelId;
    private long cost;
    private String brand;
    private long price;
    private int batteryCapacity;
}
