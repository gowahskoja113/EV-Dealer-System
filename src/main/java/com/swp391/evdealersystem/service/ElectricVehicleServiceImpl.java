package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ElectricVehicleRequest;
import com.swp391.evdealersystem.dto.response.ElectricVehicleResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Model;
import com.swp391.evdealersystem.mapper.ElectricVehicleMapper;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import com.swp391.evdealersystem.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectricVehicleServiceImpl implements ElectricVehicleService {

    private final ElectricVehicleRepository evRepo;
    private final ModelRepository modelRepo;
    private final ElectricVehicleMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ElectricVehicleResponse> getAll() {
        return evRepo.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ElectricVehicleResponse> getByModelCode(String modelCode) {
        Model model = modelRepo.findByModelCode(modelCode)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + modelCode));
        return evRepo.findByModel(model).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ElectricVehicleResponse create(ElectricVehicleRequest req) {
        Model model = (req.getModelCode() != null)
                ? modelRepo.findByModelCode(req.getModelCode())
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + req.getModelCode()))
                : modelRepo.findById(req.getModelId())
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + req.getModelId()));

        // enforce “1 model ↔ 1 EV”
        if (evRepo.existsByModel_ModelCode(model.getModelCode())) {
            throw new IllegalArgumentException("Đã tồn tại xe đại diện cho modelCode=" + model.getModelCode());
        }

        ElectricVehicle ev = mapper.toEntity(req, model);
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
        if (req.getModelCode() != null) {
            model = modelRepo.findByModelCode(req.getModelCode())
                    .orElseThrow(() -> new IllegalArgumentException("Model not found: " + req.getModelCode()));
        } else if (req.getModelId() != null) {
            model = modelRepo.findById(req.getModelId())
                    .orElseThrow(() -> new IllegalArgumentException("Model not found: " + req.getModelId()));
        }

        mapper.updateEntity(ev, req, model);
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
}
