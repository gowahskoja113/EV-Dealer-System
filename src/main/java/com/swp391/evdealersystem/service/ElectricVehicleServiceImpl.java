package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ElectricVehicleRequest;
import com.swp391.evdealersystem.dto.response.ElectricVehicleResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.mapper.ElectricVehicleMapper;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElectricVehicleServiceImpl implements ElectricVehicleService {

    private final ElectricVehicleRepository repository;

    public ElectricVehicleServiceImpl(ElectricVehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public ElectricVehicleResponse create(ElectricVehicleRequest request) {
        ElectricVehicle ev = ElectricVehicleMapper.toEntity(request);
        return ElectricVehicleMapper.toDto(repository.save(ev));
    }

    @Override
    public ElectricVehicleResponse getById(Long id) {
        ElectricVehicle ev = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ElectricVehicle not found with id " + id));
        return ElectricVehicleMapper.toDto(ev);
    }

    @Override
    public List<ElectricVehicleResponse> getAll() {
        return repository.findAll().stream()
                .map(ElectricVehicleMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ElectricVehicleResponse update(Long id, ElectricVehicleRequest request) {
        ElectricVehicle electricVehicle = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ElectricVehicle not found with id " + id));

        electricVehicle.setModel(request.getModel());
        electricVehicle.setCost(request.getCost());
        electricVehicle.setBrand(request.getBrand());
        electricVehicle.setPrice(request.getPrice());
        electricVehicle.setBatteryCapacity(request.getBatteryCapacity());

        return ElectricVehicleMapper.toDto(repository.save(electricVehicle));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}