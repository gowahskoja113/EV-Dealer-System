package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.WarehouseRequest;
import com.swp391.evdealersystem.dto.request.WarehouseStockRequest;
import com.swp391.evdealersystem.dto.response.WarehouseResponse;
import com.swp391.evdealersystem.entity.Model;
import com.swp391.evdealersystem.entity.Warehouse;
import com.swp391.evdealersystem.entity.WarehouseStock;
import com.swp391.evdealersystem.mapper.WarehouseMapper;
import com.swp391.evdealersystem.repository.ModelRepository;
import com.swp391.evdealersystem.repository.WarehouseRepository;
import com.swp391.evdealersystem.repository.WarehouseStockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepo;
    private final WarehouseStockRepository stockRepo;
    private final ModelRepository modelRepo;
    private final WarehouseMapper mapper;

    @Override
    @Transactional
    public WarehouseResponse create(WarehouseRequest request) {
        if (warehouseRepo.existsByWarehouseLocation(request.getWarehouseLocation())) {
            throw new IllegalArgumentException("Warehouse location already exists");
        }
        Warehouse w = mapper.toEntity(request);
        Warehouse saved = warehouseRepo.save(w);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponse getById(Long id) {
        Warehouse w = warehouseRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        // đảm bảo load stocks
        if (w.getStocks() != null) {
            w.getStocks().size();
        }
        return mapper.toResponse(w);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseResponse> getAll() {
        return warehouseRepo.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse w = warehouseRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        mapper.updateEntity(w, request);
        Warehouse saved = warehouseRepo.save(w);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Warehouse w = warehouseRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        warehouseRepo.delete(w);
    }

    @Override
    @Transactional
    public WarehouseResponse upsertStock(Long warehouseId, WarehouseStockRequest request) {
        Warehouse w = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        Model m = modelRepo.findById(request.getModelId())
                .orElseThrow(() -> new EntityNotFoundException("Model not found"));

        WarehouseStock stock = stockRepo.findByWarehouseAndModel(w, m)
                .orElseGet(() -> {
                    WarehouseStock s = new WarehouseStock();
                    s.setWarehouse(w);
                    s.setModel(m);
                    return s;
                });

        stock.setQuantity(request.getQuantity());
        stockRepo.save(stock);

        int total = w.getStocks().stream()
                .map(WarehouseStock::getQuantity)
                .reduce(0, Integer::sum);
        w.setVehicleQuantity(total);
        warehouseRepo.save(w);

        return mapper.toResponse(w);
    }

    @Override
    @Transactional
    public WarehouseResponse removeStock(Long warehouseId, Long modelId) {
        Warehouse w = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        Model m = modelRepo.findById(modelId)
                .orElseThrow(() -> new EntityNotFoundException("Model not found"));

        stockRepo.deleteByWarehouseAndModel(w, m);

        int total = w.getStocks().stream()
                .map(WarehouseStock::getQuantity)
                .reduce(0, Integer::sum);
        w.setVehicleQuantity(total);
        warehouseRepo.save(w);

        return mapper.toResponse(w);
    }
}
