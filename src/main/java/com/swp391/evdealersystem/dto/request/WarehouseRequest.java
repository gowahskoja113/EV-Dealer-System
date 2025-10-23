package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WarehouseRequest {
    @NotBlank(message = "Warehouse location is required")
    @Size(max = 255, message = "Location cannot exceed 255 characters")
    private String warehouseLocation;

    @NotBlank(message = "Warehouse name is required")
    @Size(max = 100, message = "Warehouse name cannot exceed 100 characters")
    private String warehouseName;

}
