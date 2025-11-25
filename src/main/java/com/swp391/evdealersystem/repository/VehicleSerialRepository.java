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

    // Phương thức cũ dùng khi lấy chi tiết Serial theo kho và model
    List<VehicleSerial> findByModel_ModelIdAndWarehouse_WarehouseIdOrderBySeqNoAsc(Long modelId, Long warehouseId);

    @Query("""
      select coalesce(max(vs.seqNo), 0) from VehicleSerial vs
      where vs.model.modelId = :modelId
    """)
    int findMaxSeqNoByModel(Long modelId);

    List<VehicleSerial> findByModel_ModelIdAndWarehouse_WarehouseIdOrderBySeqNoDesc(
            Long modelId, Long warehouseId, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT vs FROM VehicleSerial vs WHERE vs.vin = :vin")
    Optional<VehicleSerial> findByVinForUpdate(@Param("vin") String vin);

    boolean existsByVin(String vin);
    Optional<VehicleSerial> findByVin(String vin);

    List<VehicleSerial> findByWarehouse_Dealership_DealershipId(Long dealershipId);

    // Phương thức đã sửa lỗi LIMIT/FETCH, dùng để chuyển kho
    @Query("""
      select vs from VehicleSerial vs
      where vs.warehouse.warehouseId = :warehouseId and vs.model.modelId = :modelId
      and vs.status = 'AVAILABLE'
      order by vs.seqNo desc
    """)
    List<VehicleSerial> findTopNSerialsForTransfer(
            @Param("warehouseId") Long warehouseId,
            @Param("modelId") Long modelId,
            Pageable pageable);
}