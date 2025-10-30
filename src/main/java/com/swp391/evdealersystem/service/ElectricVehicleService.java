package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ElectricVehicleRequest;
import com.swp391.evdealersystem.dto.response.ElectricVehicleResponse;

import java.util.List;

public interface ElectricVehicleService {
    ElectricVehicleResponse create(ElectricVehicleRequest request);
    ElectricVehicleResponse getById(Long id);
    List<ElectricVehicleResponse> getAll();
    List<ElectricVehicleResponse> getByModelId(Long modelId);
    ElectricVehicleResponse update(Long vehicleId, ElectricVehicleRequest request);
    void delete(Long id);
    List<ElectricVehicleResponse> getByWarehouse(Long warehouseId, boolean selectableOnly);

}