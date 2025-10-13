package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ModelRequest {

    @NotBlank
    private String modelCode;

    @NotBlank
    private String brand;

    public String getModelCode() { return modelCode; }
    public void setModelCode(String modelCode) { this.modelCode = modelCode; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
}