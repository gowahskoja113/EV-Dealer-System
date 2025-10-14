package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Model;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElectricVehicleRepository extends JpaRepository <ElectricVehicle, Long> {
    @EntityGraph(attributePaths = "model")
    List<ElectricVehicle> findByModel(Model model);

    @EntityGraph(attributePaths = "model")
    List<ElectricVehicle> findAll();

    long countByModel(Model model);
}
