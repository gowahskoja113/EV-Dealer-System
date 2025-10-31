package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.entity.ElectricVehicle;

public interface VehicleStatusService {
    public ElectricVehicle placeHold(Long vehicleId, long holdMinutes);
    public ElectricVehicle releaseHoldIfExpired(Long vehicleId);
    public ElectricVehicle markSoldOut(Long vehicleId);
    public ElectricVehicle markAvailable(Long vehicleId);
}
