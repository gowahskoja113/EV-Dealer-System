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
}
