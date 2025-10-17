package com.swp391.evdealersystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class WarehouseResponse {
    private Long warehouseId;
    private String warehouseLocation;
    private Integer vehicleQuantity;
    private List<WarehouseStockResponse> items;
}
