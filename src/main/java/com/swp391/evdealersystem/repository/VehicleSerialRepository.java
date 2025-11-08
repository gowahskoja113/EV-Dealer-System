package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.VehicleSerial;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleSerialRepository extends JpaRepository<VehicleSerial, Long> {
    List<VehicleSerial> findByModel_ModelIdAndWarehouse_WarehouseIdOrderBySeqNoAsc(Long modelId, Long warehouseId);

    @Query("""
      select coalesce(max(vs.seqNo), 0) from VehicleSerial vs
      where vs.model.modelId = :modelId and vs.warehouse.warehouseId = :warehouseId
    """)
    int findMaxSeqNoByModelAndWarehouse(Long modelId, Long warehouseId);

    List<VehicleSerial> findByModel_ModelIdAndWarehouse_WarehouseIdOrderBySeqNoDesc(
            Long modelId, Long warehouseId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from VehicleSerial v where v.id = :id")
    Optional<VehicleSerial> findByIdForUpdate(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)

    @Query("SELECT vs FROM VehicleSerial vs WHERE vs.vin = :vin")
    Optional<VehicleSerial> findByVinForUpdate(@Param("vin") String vin);
}