package com.swp391.evdealersystem.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class WarehouseStockResponse {
    private Long vehicleId;
    private String modelCode;
    private String brand;
    private String color;
    private Integer productionYear;
    private Integer quantity;
}
