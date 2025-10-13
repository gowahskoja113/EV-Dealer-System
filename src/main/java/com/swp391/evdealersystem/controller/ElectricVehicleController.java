package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.ElectricVehicleRequest;
import com.swp391.evdealersystem.dto.response.ElectricVehicleResponse;
import com.swp391.evdealersystem.service.ElectricVehicleService;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/electric-vehicles")
@RequiredArgsConstructor
public class ElectricVehicleController {

    private final ElectricVehicleService service;

    // List EV theo model
    @GetMapping
    @Secured({"ROLE_ADMIN", "ROLE_EVMSTAFF"})
    public ResponseEntity<List<ElectricVehicleResponse>> listByModel(@PathVariable Long modelId) {
        return ResponseEntity.ok(service.getByModelId(modelId));
    }

    @GetMapping("/all")
    @Secured({"ROLE_ADMIN", "ROLE_EVMSTAFF"})
    public ResponseEntity<List<ElectricVehicleResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
//    @Secured({"ROLE_ADMIN"})
    @PermitAll
    public ResponseEntity<ElectricVehicleResponse> create(@Valid @RequestBody ElectricVehicleRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    // Lấy chi tiết 1 EV
    @GetMapping("/{vehicleId}")
    @Secured({"ROLE_ADMIN", "ROLE_EVMSTAFF"})
    public ResponseEntity<ElectricVehicleResponse> getById(@PathVariable Long modelId,
                                                           @PathVariable Long vehicleId) {
        // modelId ở path để giữ ngữ nghĩa; service chỉ cần vehicleId
        return ResponseEntity.ok(service.getById(vehicleId));
    }

    // Cập nhật EV (không đổi model)
    @PutMapping("/{vehicleId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ElectricVehicleResponse> update(@PathVariable Long modelId,
                                                          @PathVariable Long vehicleId,
                                                          @Valid @RequestBody ElectricVehicleRequest request) {
        return ResponseEntity.ok(service.update(vehicleId, request));
    }

    // Xóa EV
    @DeleteMapping("/{vehicleId}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable Long modelId,
                                       @PathVariable Long vehicleId) {
        service.delete(vehicleId);
        return ResponseEntity.noContent().build();
    }
}
