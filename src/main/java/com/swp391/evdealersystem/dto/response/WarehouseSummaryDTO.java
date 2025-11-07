package com.swp391.evdealersystem.dto.response;

import lombok.Data;

@Data
public class WarehouseSummaryDTO {
    private Long warehouseId;
    private String warehouseLocation;
    private String warehouseName;
    private Integer vehicleQuantity;
}