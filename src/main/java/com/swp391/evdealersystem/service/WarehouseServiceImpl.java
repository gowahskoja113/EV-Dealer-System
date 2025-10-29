package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.WarehouseRequest;
import com.swp391.evdealersystem.dto.request.WarehouseStockRequest;
import com.swp391.evdealersystem.dto.response.WarehouseResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Warehouse;
import com.swp391.evdealersystem.entity.WarehouseStock;
import com.swp391.evdealersystem.mapper.WarehouseMapper;
import com.swp391.evdealersystem.repository.WarehouseRepository;
import com.swp391.evdealersystem.repository.WarehouseStockRepository;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import com.swp391.evdealersystem.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepo;
    private final WarehouseStockRepository stockRepo;
    private final ElectricVehicleRepository vehicleRepo;
    private final WarehouseMapper mapper;

    @Override
    @Transactional
    public WarehouseResponse create(WarehouseRequest request) {
        if (warehouseRepo.existsByWarehouseLocation(request.getWarehouseLocation())) {
            throw new IllegalArgumentException("Warehouse location already exists");
        }
        Warehouse saved = warehouseRepo.save(mapper.toEntity(request));
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WarehouseResponse getById(Long id) {
        Warehouse w = warehouseRepo.findWithStocksById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        return mapper.toResponse(w);
    }

    @Override
    @Transactional
    public List<WarehouseResponse> getAll() {
        return warehouseRepo.findAllWithStocks()
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse w = warehouseRepo.findWithStocksById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        mapper.updateEntity(w, request);
        return mapper.toResponse(warehouseRepo.save(w));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Warehouse w = warehouseRepo.findWithStocksById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        warehouseRepo.delete(w);
    }

    @Override
    @Transactional
    public WarehouseResponse upsertStock(Long warehouseId, WarehouseStockRequest request) {
        Warehouse w = warehouseRepo.findWithStocksById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        ElectricVehicle v = vehicleRepo.findById(request.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));

        WarehouseStock stock = stockRepo.findByWarehouseAndVehicle(w, v)
                .orElseGet(() -> {
                    WarehouseStock s = new WarehouseStock();
                    s.setWarehouse(w);
                    s.setVehicle(v);
                    w.getStocks().add(s);
                    return s;
                });

        stock.setQuantity(request.getQuantity());
        stockRepo.save(stock);

        int total = w.getStocks().stream()
                .map(WarehouseStock::getQuantity)
                .reduce(0, Integer::sum);
        w.setVehicleQuantity(total);
        warehouseRepo.save(w);

        Warehouse refreshed = warehouseRepo.findWithStocksById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        return mapper.toResponse(refreshed);
    }

    @Override
    @Transactional
    public WarehouseResponse removeStock(Long warehouseId, Long modelId /* vehicleId */) {
        Warehouse w = warehouseRepo.findWithStocksById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        ElectricVehicle v = vehicleRepo.findById(modelId)
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));

        stockRepo.deleteByWarehouseAndVehicle(w, v);
        w.getStocks().removeIf(s -> s.getVehicle().getVehicleId().equals(modelId));

        int total = w.getStocks().stream()
                .map(WarehouseStock::getQuantity)
                .reduce(0, Integer::sum);
        w.setVehicleQuantity(total);
        warehouseRepo.save(w);

        Warehouse refreshed = warehouseRepo.findWithStocksById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
        return mapper.toResponse(refreshed);
    }
}
