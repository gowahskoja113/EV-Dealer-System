package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.CashPaymentRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.service.CashPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class CashPaymentController {

    private final CashPaymentService cashPaymentService;

    @PostMapping("/cash/{orderId}")
    public ResponseEntity<OrderResponse> payCash(
            @PathVariable Long orderId,
            @Valid @RequestBody CashPaymentRequest req
    ) {
        OrderResponse res = cashPaymentService.processCash(orderId, req);
        return ResponseEntity.ok(res);
    }
}
