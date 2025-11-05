package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.OrderDepositRequest;
import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.response.DepositOrderView;
import com.swp391.evdealersystem.dto.response.OrderDepositResponse;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.service.OrderQueryService;
import com.swp391.evdealersystem.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderQueryService orderQueryService;

    @PostMapping("/deposit")
    public ResponseEntity<OrderDepositResponse> createDeposit(@Valid @RequestBody OrderDepositRequest req) {
        OrderDepositResponse res = orderService.createDepositOrder(req);
        return ResponseEntity.created(URI.create("/api/orders/" + res.getOrderId())).body(res);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @PostMapping("/{orderId}/pay-remaining")
    public ResponseEntity<OrderResponse> payRemaining(@PathVariable Long orderId,
                                                      @Valid @RequestBody OrderRequest req) {
        OrderResponse res = orderService.payRemaining(orderId, req);
        return ResponseEntity.ok(res);
    }

    @GetMapping({"/deposit/{customerId}", "/deposit/{customerId}/{orderId}"})
    public ResponseEntity<List<DepositOrderView>> getDepositedOrders(
            @PathVariable Long customerId,
            @RequestParam(required = false) Long orderId
    ) {
        List<DepositOrderView> data = orderQueryService.getDepositedOrders(customerId, orderId);
        return ResponseEntity.ok(data);
    }
}