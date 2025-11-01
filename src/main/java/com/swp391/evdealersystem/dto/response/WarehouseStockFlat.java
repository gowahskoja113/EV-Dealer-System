package com.swp391.evdealersystem.dto.response;

public record WarehouseStockFlat(
        Long modelId,
        String modelCode,
        String brand,
        String color,
        Integer productionYear,
        Integer quantity
) {}
