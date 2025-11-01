package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.VehicleSerial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VehicleSerialRepository extends JpaRepository<VehicleSerial, Long> {
    List<VehicleSerial> findByModel_ModelIdAndWarehouse_WarehouseIdOrderBySeqNoAsc(Long modelId, Long warehouseId);

    @Query("""
      select coalesce(max(vs.seqNo), 0) from VehicleSerial vs
      where vs.model.modelId = :modelId and vs.warehouse.warehouseId = :warehouseId
    """)
    int findMaxSeqNoByModelAndWarehouse(Long modelId, Long warehouseId);

    List<VehicleSerial> findByModel_ModelIdAndWarehouse_WarehouseIdOrderBySeqNoDesc(
            Long modelId, Long warehouseId, Pageable pageable);
}