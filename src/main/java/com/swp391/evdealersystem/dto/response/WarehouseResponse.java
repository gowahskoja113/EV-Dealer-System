package com.swp391.evdealersystem.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseResponse {
    private Long warehouseId;
    private String warehouseLocation;
    private Integer vehicleQuantity;
    private String warehouseName;
    private List<WarehouseStockResponse> items;
}
