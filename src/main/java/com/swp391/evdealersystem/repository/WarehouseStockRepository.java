package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.dto.response.WarehouseStockFlat;
import com.swp391.evdealersystem.entity.Model;
import com.swp391.evdealersystem.entity.Warehouse;
import com.swp391.evdealersystem.entity.WarehouseStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Long> {
    Optional<WarehouseStock> findByWarehouseAndModel(Warehouse warehouse, Model model);
    void deleteByWarehouseAndModel(Warehouse warehouse, Model model);
    boolean existsByWarehouseAndModel(Warehouse warehouse, Model model);

    @Query("""
       select new com.swp391.evdealersystem.dto.response.WarehouseStockFlat(
           m.modelId, m.modelCode, m.brand, m.color, m.productionYear, s.quantity
       )
       from WarehouseStock s
       join s.model m
       where s.warehouse.warehouseId = :warehouseId
       order by m.modelCode
    """)
    List<WarehouseStockFlat> findFlatByWarehouseId(Long warehouseId);

    @Query("""
       select coalesce(sum(s.quantity), 0)
       from WarehouseStock s
       where s.warehouse.warehouseId = :warehouseId
    """)
    Integer sumQuantityByWarehouseId(Long warehouseId);
}
