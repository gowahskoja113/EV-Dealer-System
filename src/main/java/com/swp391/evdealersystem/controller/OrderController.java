package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;
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

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        OrderResponse res = orderService.create(request);
        return ResponseEntity.created(URI.create("/api/orders/" + res.getOrderId())).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/by-customer/{customerId}")
    public ResponseEntity<List<OrderResponse>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getByCustomerId(customerId));
    }

    @GetMapping("/by-vehicle/{vehicleId}")
    public ResponseEntity<List<OrderResponse>> getByVehicle(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(orderService.getByVehicleId(vehicleId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}