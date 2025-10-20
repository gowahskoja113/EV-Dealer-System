package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.CustomerRequest;
import com.swp391.evdealersystem.dto.response.CustomerResponse;
import com.swp391.evdealersystem.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // CREATE
    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        CustomerResponse resp = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // UPDATE
    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
        return customerService.updateCustomer(id, request);
    }

    // READ by ID
    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    // READ by phone
    @GetMapping("/phone/{phoneNumber}")
    public CustomerResponse getByPhone(@PathVariable String phoneNumber) {
        return customerService.getCustomerByPhone(phoneNumber);
    }

    // READ all
    @GetMapping
    public List<CustomerResponse> getAll() {
        return customerService.getAllCustomers();
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }

    @PatchMapping("/{id}/assign-sales/{userId}")
    public CustomerResponse assignSales(@PathVariable Long id, @PathVariable Long userId) {
        return customerService.assignSales(id, userId);
    }

    @PatchMapping("/{id}/unassign-sales")
    public CustomerResponse unassignSales(@PathVariable Long id) {
        return customerService.unassignSales(id);
    }
}

