package com.swp391.evdealersystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class WarehouseStockResponse {
    private Long modelId;
    private String modelCode;
    private String brand;
    private Integer quantity;
}
