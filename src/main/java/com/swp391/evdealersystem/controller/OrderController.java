package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.OrderDepositRequest;
import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.response.OrderDepositResponse;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/deposit")
    public ResponseEntity<OrderDepositResponse> createDeposit(@Valid @RequestBody OrderDepositRequest req) {
        OrderDepositResponse res = orderService.createDepositOrder(req);
        return ResponseEntity.created(URI.create("/api/orders/" + res.getOrderId())).body(res);
    }

    @PostMapping("/{orderId}/pay-remaining")
    public ResponseEntity<OrderResponse> payRemaining(@PathVariable Long orderId,
                                                      @Valid @RequestBody OrderRequest req) {
        OrderResponse res = orderService.payRemaining(orderId, req);
        return ResponseEntity.ok(res);
    }
}