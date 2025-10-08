package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.ElectricVehicleRequest;
import com.swp391.evdealersystem.dto.response.ElectricVehicleResponse;
import com.swp391.evdealersystem.service.ElectricVehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/electric-vehicles")
public class ElectricVehicleController {

    private final ElectricVehicleService service;

    public ElectricVehicleController(ElectricVehicleService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @Secured({"ROLE_ADMIN", "ROLE_EVMSTAFF"})
    public ResponseEntity<ElectricVehicleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Secured({"ROLE_ADMIN", "ROLE_EVMSTAFF"})
    public ResponseEntity<List<ElectricVehicleResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ElectricVehicleResponse> create(@RequestBody ElectricVehicleRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<ElectricVehicleResponse> update(@PathVariable Long id,
                                                          @RequestBody ElectricVehicleRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Secured({"ROLE_ADMIN"})
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
