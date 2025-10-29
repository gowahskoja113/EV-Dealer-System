package com.swp391.evdealersystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class WarehouseStockResponse {
    private Long vehicleId;
    private String modelCode;
    private String brand;
    private String color;
    private Integer productionYear;
    private Integer quantity;
}
