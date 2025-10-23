package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ModelRequest {

    @NotBlank
    private String modelCode;

    @NotBlank
    private String brand;

    @NotBlank
    private String color;

    @NotNull(message = "Production year is required")
    @Min(value = 1886, message = "Production year must be >= 1886")
    @Max(value = 2100, message = "Production year is unrealistically high")
    private Integer productionYear;
}