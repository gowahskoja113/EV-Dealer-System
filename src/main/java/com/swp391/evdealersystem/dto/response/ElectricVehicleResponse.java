package com.swp391.evdealersystem.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ElectricVehicleResponse {
    private Long vehicleId;
    private String brand;
    private long cost;
    private long price;
    private int batteryCapacity;

    // Có thể trả thêm thông tin model nếu cần hiển thị
    private Long modelId;
    private String modelName;
}
