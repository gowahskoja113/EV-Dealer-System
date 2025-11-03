package com.swp391.evdealersystem.controller;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // ====== B1: Tạo hợp đồng đặt cọc ======
    @PostMapping("/deposit")
    public ResponseEntity<OrderDepositResponse> createDeposit(@Valid @RequestBody OrderDepositRequest req) {
        OrderDepositResponse res = orderService.createDepositOrder(req);
        return ResponseEntity.created(URI.create("/api/orders/" + res.getOrderId())).body(res);
    }

    // ====== B2: Thanh toán phần còn lại ======
    @PostMapping("/{orderId}/pay-remaining")
    public ResponseEntity<OrderResponse> payRemaining(@PathVariable Long orderId,
                                                      @Valid @RequestBody OrderRequest req) {
        OrderResponse res = orderService.payRemaining(orderId, req);
        return ResponseEntity.ok(res);
    }

    // ====== CRUD / Queries ======
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Exception
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler({ IllegalArgumentException.class, MethodArgumentTypeMismatchException.class })
    public ResponseEntity<String> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
