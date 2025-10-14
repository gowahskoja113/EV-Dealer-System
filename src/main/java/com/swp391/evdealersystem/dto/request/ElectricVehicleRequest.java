package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.NotNull;

public class ElectricVehicleRequest {

    @NotNull
    private String modelCode;

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    @NotNull
    private Long cost;
    @NotNull
    private Long price;
    @NotNull
    private Integer batteryCapacity;

    public Long getCost() { return cost; }
    public void setCost(Long cost) { this.cost = cost; }
    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }
    public Integer getBatteryCapacity() { return batteryCapacity; }
    public void setBatteryCapacity(Integer batteryCapacity) { this.batteryCapacity = batteryCapacity; }
}
