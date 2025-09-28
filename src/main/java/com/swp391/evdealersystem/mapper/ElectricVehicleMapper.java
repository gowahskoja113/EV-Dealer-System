package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.ElectricVehicleRequest;
import com.swp391.evdealersystem.dto.response.ElectricVehicleResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;

public class ElectricVehicleMapper {

    public static ElectricVehicle toEntity(ElectricVehicleRequest request) {
        ElectricVehicle electricVehicle = new ElectricVehicle();
        electricVehicle.setModel(request.getModel());
        electricVehicle.setCost(request.getCost());
        electricVehicle.setBrand(request.getBrand());
        electricVehicle.setPrice(request.getPrice());
        electricVehicle.setBatteryCapacity(request.getBatteryCapacity());
        return electricVehicle;
    }

    public static ElectricVehicleResponse toDto(ElectricVehicle electricVehicle) {
        ElectricVehicleResponse dto = new ElectricVehicleResponse();
        dto.setVehicleId(electricVehicle.getVehicleId());
        dto.setModel(electricVehicle.getModel());
        dto.setCost(electricVehicle.getCost());
        dto.setBrand(electricVehicle.getBrand());
        dto.setPrice(electricVehicle.getPrice());
        dto.setBatteryCapacity(electricVehicle.getBatteryCapacity());
        return dto;
    }
}