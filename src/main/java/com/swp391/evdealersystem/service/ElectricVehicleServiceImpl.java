package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ElectricVehicleRequest;
import com.swp391.evdealersystem.dto.response.ElectricVehicleResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Model;
import com.swp391.evdealersystem.mapper.ElectricVehicleMapper;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import com.swp391.evdealersystem.repository.ModelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectricVehicleServiceImpl implements ElectricVehicleService {

    private final ElectricVehicleRepository evRepo;
    private final ModelRepository modelRepo;
    private final ElectricVehicleMapper electricVehicleMapper;

    @Override
    @Transactional
    public ElectricVehicleResponse create(ElectricVehicleRequest req) {
        Model model = modelRepo.findByModelCode(req.getModelCode())
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + req.getModelCode()));

        ElectricVehicle ev = electricVehicleMapper.toEntity(req, model);
        ev = evRepo.save(ev);
        return electricVehicleMapper.toResponse(ev);
    }

    @Override
    public ElectricVehicleResponse getById(Long vehicleId) {
        ElectricVehicle ev = evRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));
        return electricVehicleMapper.toResponse(ev);
    }

    @Override
    public List<ElectricVehicleResponse> getAll() {
        return evRepo.findAll().stream().map(electricVehicleMapper::toResponse).toList();
    }

    @Override
    public List<ElectricVehicleResponse> getByModelId(Long modelId) {
        Model model = modelRepo.findById(modelId)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + modelId));
        return evRepo.findByModel(model).stream().map(electricVehicleMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ElectricVehicleResponse update(Long id, ElectricVehicleRequest request) {
        ElectricVehicle electricVehicle = evRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("ElectricVehicle not found with id " + id));

        // Không đổi model trong update
        electricVehicleMapper.updateEntity(electricVehicle, request);
        electricVehicle = evRepo.save(electricVehicle);
        return electricVehicleMapper.toResponse(electricVehicle);
    }

    @Override
    @Transactional
    public void delete(Long vehicleId) {
        ElectricVehicle ev = evRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vehicleId));
        evRepo.delete(ev);
    }
}
