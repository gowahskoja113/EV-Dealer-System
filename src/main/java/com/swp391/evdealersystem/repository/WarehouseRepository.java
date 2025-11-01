package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    boolean existsByWarehouseLocation(String warehouseLocation);

    @Query("""
              select distinct w from Warehouse w
              left join fetch w.stocks s
              left join fetch s.model m
              order by w.warehouseId
            """)
    List<Warehouse> findAllWithStocks();

    @Query("""
              select w from Warehouse w
              left join fetch w.stocks s
              left join fetch s.model m
              where w.warehouseId = :warehouseId
            """)
    Optional<Warehouse> findWithStocksById(Long warehouseId);
}
