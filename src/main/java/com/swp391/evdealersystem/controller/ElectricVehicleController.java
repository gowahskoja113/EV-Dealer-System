package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.ElectricVehicleRequest;
import com.swp391.evdealersystem.dto.response.ElectricVehicleResponse;
import com.swp391.evdealersystem.mapper.ElectricVehicleMapper;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import com.swp391.evdealersystem.repository.ModelRepository;
import com.swp391.evdealersystem.repository.WarehouseRepository;
import com.swp391.evdealersystem.service.ElectricVehicleService;
import com.swp391.evdealersystem.service.VehicleStatusService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/electric-vehicles")
@RequiredArgsConstructor
public class ElectricVehicleController {

    private final ElectricVehicleService service;
    private final ElectricVehicleRepository evRepo;
    private final ModelRepository modelRepo;
    private final WarehouseRepository warehouseRepo;
    private final ElectricVehicleMapper vehicleMapper;

    @GetMapping("/search-by-modelCode/{modelCode}")
    @PreAuthorize("hasAnyRole('ADMIN','EVMSTAFF')")
    public ResponseEntity<List<ElectricVehicleResponse>> searchByModelCode(@PathVariable String modelCode) {
        return ResponseEntity.ok(service.getByModelCode(modelCode));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','EVMSTAFF')")
    public ResponseEntity<List<ElectricVehicleResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ElectricVehicleResponse> create(@Valid @RequestBody ElectricVehicleRequest req) {
        if (req.getModelCode() == null && req.getModelId() == null) {
            throw new IllegalArgumentException("modelCode hoặc modelId là bắt buộc");
        }
        // enforce 1 model ↔ 1 EV
        String code = req.getModelCode() != null
                ? req.getModelCode()
                : modelRepo.findById(req.getModelId())
                .orElseThrow(() -> new EntityNotFoundException("Model not found"))
                .getModelCode();

        if (evRepo.existsByModel_ModelCode(code)) {
            throw new IllegalArgumentException("Đã tồn tại xe đại diện cho modelCode=" + code);
        }

        // Ủy quyền cho service để tái sử dụng mapper/logic chung
        ElectricVehicleResponse saved = service.create(req);
        return ResponseEntity.created(URI.create("/api/electric-vehicles/" + saved.getVehicleId())).body(saved);
    }

    @GetMapping("/{vehicleId}")
    @PreAuthorize("hasAnyRole('ADMIN','EVMSTAFF')")
    public ResponseEntity<ElectricVehicleResponse> getById(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(service.getById(vehicleId));
    }

    @PutMapping("/{vehicleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ElectricVehicleResponse> update(@PathVariable Long vehicleId,
                                                          @Valid @RequestBody ElectricVehicleRequest request) {
        return ResponseEntity.ok(service.update(vehicleId, request));
    }

    @DeleteMapping("/{vehicleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long vehicleId) {
        service.delete(vehicleId);
        return ResponseEntity.noContent().build();
    }
}
