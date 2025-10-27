package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.GeneratePlanRequest;
import com.swp391.evdealersystem.dto.response.InstallmentResponse;
import com.swp391.evdealersystem.service.InstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/{orderId}/installments")
@RequiredArgsConstructor
public class InstallmentController {

    private final InstallmentService installmentService;

    @PostMapping("/generate")
    @Secured({"ROLE_ADMIN", "ROLE_EVMSTAFF"})
    public ResponseEntity<List<InstallmentResponse>> generate(
            @PathVariable Long orderId,
            @RequestBody GeneratePlanRequest req) {
        return ResponseEntity.ok(installmentService.generatePlan(orderId, req));
    }

    @GetMapping
    @Secured({"ROLE_ADMIN", "ROLE_EVMSTAFF"})
    public ResponseEntity<List<InstallmentResponse>> list(@PathVariable Long orderId) {
        return ResponseEntity.ok(installmentService.listByOrder(orderId));
    }
}
