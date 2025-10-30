package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Model;
import com.swp391.evdealersystem.enums.VehicleStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ElectricVehicleRepository extends JpaRepository<ElectricVehicle, Long> {

    @EntityGraph(attributePaths = {"model", "warehouse"})
    List<ElectricVehicle> findByModel(Model model);

    @Override
    @EntityGraph(attributePaths = {"model", "warehouse"})
    List<ElectricVehicle> findAll();

    @Override
    @EntityGraph(attributePaths = {"model", "warehouse"})
    Optional<ElectricVehicle> findById(Long vehicleId);

    long countByModel(Model model);

    @EntityGraph(attributePaths = {"model", "warehouse"})
    @Query("""
           SELECT ev FROM ElectricVehicle ev
           WHERE ev.warehouse.warehouseId = :warehouseId
             AND (
                  ev.status = com.swp391.evdealersystem.enums.VehicleStatus.AVAILABLE
                  OR (ev.status = com.swp391.evdealersystem.enums.VehicleStatus.HOLD
                      AND (ev.holdUntil IS NULL OR ev.holdUntil < :now))
             )
           """)
    List<ElectricVehicle> findSelectableInWarehouse(@Param("warehouseId") Long warehouseId,
                                                    @Param("now") OffsetDateTime now);

    @EntityGraph(attributePaths = {"model", "warehouse"})
    Optional<ElectricVehicle> findByVehicleIdAndStatus(Long vehicleId, VehicleStatus status);

    @EntityGraph(attributePaths = {"model", "warehouse"})
    List<ElectricVehicle> findByWarehouse_WarehouseId(Long warehouseId);

}
