package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElectricVehicleRepository extends JpaRepository <ElectricVehicle, Long> {
    List<ElectricVehicle> findByModel(Model model);
    long countByModel(Model model);
}
