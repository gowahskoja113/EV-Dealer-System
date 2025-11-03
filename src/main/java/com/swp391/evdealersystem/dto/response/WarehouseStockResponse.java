package com.swp391.evdealersystem.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class WarehouseStockResponse {
    private String modelCode;
    private String brand;
    private String color;
    private Integer productionYear;
    private Integer quantity;

    private List<VehicleSerialResponse> serials;
}