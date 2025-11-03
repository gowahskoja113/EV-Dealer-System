package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.entity.ElectricVehicle;

public interface VehicleStatusService {
    public ElectricVehicle markSoldOut(Long vehicleId);
    public ElectricVehicle markAvailable(Long vehicleId);
}
