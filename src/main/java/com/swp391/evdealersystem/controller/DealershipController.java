package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.DealershipRequest;
import com.swp391.evdealersystem.dto.request.TransferWarehouseRequest;
import com.swp391.evdealersystem.dto.response.DealershipResponse;
import com.swp391.evdealersystem.enums.DealershipStatus;
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

    @PostMapping
    public ResponseEntity<DealershipResponse> createDealership(@Valid @RequestBody DealershipRequest request) {
        DealershipResponse response = dealershipService.createDealership(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DealershipResponse> updateDealershipStatus(
            @PathVariable Long id,
            @RequestParam DealershipStatus status) {

        DealershipResponse response = dealershipService.changeStatus(id, status);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DealershipResponse> getDealershipById(@PathVariable Long id) {
        DealershipResponse response = dealershipService.getDealershipById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DealershipResponse>> getAllDealerships() {
        List<DealershipResponse> responseList = dealershipService.getAllDealerships();
        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/{sourceId}/transfer-warehouses")
    public ResponseEntity<String> transferSelectedWarehouses(
            @PathVariable Long sourceId,
            @RequestBody TransferWarehouseRequest request) {

        // Validate cơ bản nếu cần
        if (request.getWarehouseIds() == null || request.getWarehouseIds().isEmpty()) {
            return ResponseEntity.badRequest().body("Danh sách kho cần chuyển không được trống.");
        }

        // 1. Gọi phương thức Service mới
        dealershipService.transferSelectedWarehouses(
                sourceId,
                request.getTargetDealershipId(),
                request.getWarehouseIds() // 2. Truyền thêm danh sách ID kho
        );

        return ResponseEntity.ok("Chuyển giao các kho được chọn thành công!");
    }

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