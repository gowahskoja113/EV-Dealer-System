package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.ElectricVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectricVehicleRepository extends JpaRepository <ElectricVehicle, Long> {
}
