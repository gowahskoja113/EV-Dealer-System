package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ElectricVehicleRequest;
import com.swp391.evdealersystem.dto.response.ElectricVehicleResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Model;
import com.swp391.evdealersystem.entity.Warehouse;
import com.swp391.evdealersystem.exception.NotFoundException;
import com.swp391.evdealersystem.mapper.ElectricVehicleMapper;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import com.swp391.evdealersystem.repository.ModelRepository;
import com.swp391.evdealersystem.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectricVehicleServiceImpl implements ElectricVehicleService {

    private final ElectricVehicleRepository evRepo;
    private final ModelRepository modelRepo;
    private final WarehouseRepository warehouseRepo;
    private final ElectricVehicleMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ElectricVehicleResponse> getAll() {
        return evRepo.findAll().stream().map(mapper::toResponse).toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ElectricVehicleResponse> getByModelId(Long modelId) {
        Model model = modelRepo.findById(modelId)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + modelId));
        List<ElectricVehicle> vehicles = evRepo.findByModel(model);
        return vehicles.stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ElectricVehicleResponse create(ElectricVehicleRequest req) {

        Model model = modelRepo.findById(req.getModelId())
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + req.getModelId()));

        Warehouse warehouse = warehouseRepo.findById(req.getWarehouseId())
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found: " + req.getWarehouseId()));

        ElectricVehicle ev = mapper.toEntity(req, model, warehouse);
        ev = evRepo.save(ev);
        return mapper.toResponse(ev);
    }

    @Override
    @Transactional(readOnly = true)
    public ElectricVehicleResponse getById(Long vehicleId) {
        ElectricVehicle ev = evRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));
        return mapper.toResponse(ev);
    }

    @Override
    @Transactional
    public ElectricVehicleResponse update(Long vehicleId, ElectricVehicleRequest req) {
        ElectricVehicle ev = evRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));

        Model model = null;
        Warehouse warehouse = null;

        if (req.getModelCode() != null) {
            model = modelRepo.findByModelCode(req.getModelCode())
                    .orElseThrow(() -> new IllegalArgumentException("Model not found: " + req.getModelCode()));
        }

        else if (req.getModelId() != null) {
            model = modelRepo.findById(req.getModelId())
                    .orElseThrow(() -> new IllegalArgumentException("Model not found: " + req.getModelId()));
        }

        if (req.getWarehouseId() != null) {
            warehouse = warehouseRepo.findById(req.getWarehouseId())
                    .orElseThrow(() -> new IllegalArgumentException("Warehouse not found: " + req.getWarehouseId()));
        }

        mapper.updateEntity(ev, req, model, warehouse);
        ev = evRepo.save(ev);
        return mapper.toResponse(ev);
    }

    @Override
    @Transactional
    public void delete(Long vehicleId) {
        if (!evRepo.existsById(vehicleId)) {
            throw new IllegalArgumentException("Vehicle not found: " + vehicleId);
        }
        evRepo.deleteById(vehicleId);
    }

    @Override
    public List<ElectricVehicleResponse> getByWarehouse(Long warehouseId, boolean selectableOnly) {
        warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new NotFoundException("Warehouse not found with id = " + warehouseId));

        List<ElectricVehicle> vehicles = selectableOnly
                ? evRepo.findSelectableInWarehouse(warehouseId, OffsetDateTime.now())
                : evRepo.findByWarehouse_WarehouseId(warehouseId);

        return vehicles.stream()
                .map(mapper::toResponse)
                .toList();
    }
}
