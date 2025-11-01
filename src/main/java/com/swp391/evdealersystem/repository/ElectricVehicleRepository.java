package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Model;
import com.swp391.evdealersystem.enums.VehicleStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ElectricVehicleRepository extends JpaRepository<ElectricVehicle, Long> {

    @EntityGraph(attributePaths = {"model"})
    List<ElectricVehicle> findByModel(Model model);

    Optional<ElectricVehicle> findByModel_ModelCode(String modelCode);
    boolean existsByModel_ModelCode(String modelCode);

    @Override
    @EntityGraph(attributePaths = {"model"})
    List<ElectricVehicle> findAll();

    @Override
    @EntityGraph(attributePaths = {"model"})
    Optional<ElectricVehicle> findById(Long vehicleId);

    long countByModel(Model model);

    @EntityGraph(attributePaths = {"model"})
    Optional<ElectricVehicle> findByVehicleIdAndStatus(Long vehicleId, VehicleStatus status);

    @EntityGraph(attributePaths = {"model", "warehouse"})
    List<ElectricVehicle> findByWarehouse_WarehouseId(Long warehouseId);

}

