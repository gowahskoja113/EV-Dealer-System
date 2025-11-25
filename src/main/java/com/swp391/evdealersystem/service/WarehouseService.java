package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.WarehouseRequest;
import com.swp391.evdealersystem.dto.request.WarehouseStockRequest;
import com.swp391.evdealersystem.dto.response.WarehouseResponse;
import com.swp391.evdealersystem.dto.request.TransferStockRequest;

import java.util.List;

public interface WarehouseService {
    WarehouseResponse create(WarehouseRequest request);
    WarehouseResponse getById(Long id);
    List<WarehouseResponse> getAll();
    WarehouseResponse update(Long id, WarehouseRequest request);
    void delete(Long id);
    WarehouseResponse transferStock(Long sourceWarehouseId, Long targetWarehouseId,
                                    TransferStockRequest request);
    WarehouseResponse upsertStock(Long warehouseId, WarehouseStockRequest request);
    public WarehouseResponse removeStock(Long warehouseId, String modelCode);
}
