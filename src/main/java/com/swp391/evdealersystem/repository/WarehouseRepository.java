package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    boolean existsByWarehouseLocation(String warehouseLocation);

    @Query("""
           select w
           from Warehouse w
           order by w.warehouseId
           """)
    List<Warehouse> findAllHeaders();

    @Query("""
           select w
           from Warehouse w
           where w.warehouseId = :warehouseId
           """)
    Optional<Warehouse> findHeaderById(Long warehouseId);
}
