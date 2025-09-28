package com.swp391.evdealersystem.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ElectricVehicleResponse {
    private Long vehicleId;
    private String model;
    private long cost;
    private String brand;
    private long price;
    private int batteryCapacity;
}
