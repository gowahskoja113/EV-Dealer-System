package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.DealershipRequest;
import com.swp391.evdealersystem.dto.response.DealershipResponse;
import com.swp391.evdealersystem.service.DealershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dealerships")
@RequiredArgsConstructor
public class DealershipController {

    private final DealershipService dealershipService;

    // CREATE
    @PostMapping
    public ResponseEntity<DealershipResponse> createDealership(@Valid @RequestBody DealershipRequest request) {
        DealershipResponse response = dealershipService.createDealership(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ (Get One)
    @GetMapping("/{id}")
    public ResponseEntity<DealershipResponse> getDealershipById(@PathVariable Long id) {
        DealershipResponse response = dealershipService.getDealershipById(id);
        return ResponseEntity.ok(response);
    }

    // READ (Get All)
    @GetMapping
    public ResponseEntity<List<DealershipResponse>> getAllDealerships() {
        List<DealershipResponse> responseList = dealershipService.getAllDealerships();
        return ResponseEntity.ok(responseList);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<DealershipResponse> updateDealership(@PathVariable Long id, @Valid @RequestBody DealershipRequest request) {
        DealershipResponse response = dealershipService.updateDealership(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDealership(@PathVariable Long id) {
        dealershipService.deleteDealership(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{dealershipId}/warehouses/{warehouseId}")
    public ResponseEntity<Void> deleteWarehouseFromDealership(
            @PathVariable Long dealershipId,
            @PathVariable Long warehouseId) {

        dealershipService.deleteWarehouseFromDealership(dealershipId, warehouseId);
        return ResponseEntity.noContent().build();
    }
}