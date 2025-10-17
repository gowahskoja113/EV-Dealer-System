package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Warehouse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    boolean existsByWarehouseLocation(String warehouseLocation);

    @EntityGraph(attributePaths = {"stocks", "stocks.model"})
    List<Warehouse> findAll();

    @EntityGraph(attributePaths = {"stocks", "stocks.model"})
    Warehouse findWithStocksByWarehouseId(Long warehouseId);
}
