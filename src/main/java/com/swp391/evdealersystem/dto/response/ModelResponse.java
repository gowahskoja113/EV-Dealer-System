package com.swp391.evdealersystem.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ModelResponse {
    private Long modelId;
    private String modelCode;
    private String name;

    private List<ElectricVehicleSummary> vehicles;

    @Getter
    @Setter
    public static class ElectricVehicleSummary {
        private Long vehicleId;
        private String brand;
        private int batteryCapacity;
        private long price;
    }
}
