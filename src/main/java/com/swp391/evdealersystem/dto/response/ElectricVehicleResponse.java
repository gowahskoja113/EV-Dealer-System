package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.VehicleStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

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

    private Long warehouseId;
    private String imageUrl;
    private VehicleStatus status;
    private OffsetDateTime holdUntil;
    private boolean selectableNow;
}
