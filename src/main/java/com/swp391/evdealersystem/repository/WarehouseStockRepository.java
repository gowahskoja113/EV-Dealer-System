package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Model;
import com.swp391.evdealersystem.entity.Warehouse;
import com.swp391.evdealersystem.entity.WarehouseStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Long> {
    Optional<WarehouseStock> findByWarehouseAndVehicle(Warehouse warehouse, ElectricVehicle vehicle);
    void deleteByWarehouseAndVehicle(Warehouse warehouse, ElectricVehicle vehicle);
    boolean existsByWarehouseAndVehicle(Warehouse warehouse, ElectricVehicle vehicle);
}
