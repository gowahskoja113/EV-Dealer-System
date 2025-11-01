package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.WarehouseRequest;
import com.swp391.evdealersystem.dto.request.WarehouseStockRequest;
import com.swp391.evdealersystem.dto.response.WarehouseResponse;
import com.swp391.evdealersystem.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<WarehouseResponse> create(@Valid @RequestBody WarehouseRequest request) {
        WarehouseResponse res = warehouseService.create(request);
        return ResponseEntity.created(URI.create("/api/warehouses/" + res.getWarehouseId()))
                .body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<WarehouseResponse>> getAll() {
        return ResponseEntity.ok(warehouseService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(warehouseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/stocks")
    public ResponseEntity<WarehouseResponse> upsertStock(@PathVariable Long id,
                                                         @Valid @RequestBody WarehouseStockRequest request) {
        return ResponseEntity.ok(warehouseService.upsertStock(id, request));
    }

    @DeleteMapping("/{id}/stocks/{modelCode}")
    public ResponseEntity<WarehouseResponse> removeStock(@PathVariable Long id,
                                                         @PathVariable String modelCode) {
        return ResponseEntity.ok(warehouseService.removeStock(id, modelCode));
    }
}
