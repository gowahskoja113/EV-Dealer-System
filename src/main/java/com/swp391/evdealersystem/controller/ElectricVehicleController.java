package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.ElectricVehicleRequest;
import com.swp391.evdealersystem.dto.response.ElectricVehicleResponse;
import com.swp391.evdealersystem.service.ElectricVehicleService;
import com.swp391.evdealersystem.service.VehicleStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/electric-vehicles")
@RequiredArgsConstructor
public class ElectricVehicleController {

    private final ElectricVehicleService service;
    private final VehicleStatusService statusService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','EVMSTAFF')")
    public ResponseEntity<List<ElectricVehicleResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ElectricVehicleResponse> create(@Valid @RequestBody ElectricVehicleRequest request) {
        ElectricVehicleResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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

    @GetMapping("/by-warehouse/{warehouseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ElectricVehicleResponse>> listByWarehouse(
            @PathVariable Long warehouseId,
            @RequestParam(defaultValue = "false") boolean selectableOnly) {

        return ResponseEntity.ok(service.getByWarehouse(warehouseId, selectableOnly));
    }

    @GetMapping("/search-by-modelId")
    public ResponseEntity<List<ElectricVehicleResponse>> searchByModelId(@RequestParam Long modelId) {
        return ResponseEntity.ok(service.getByModelId(modelId));
    }
}
