package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse create(OrderRequest request);
    OrderResponse getById(Long id);
    List<OrderResponse> getAll();
    List<OrderResponse> getByCustomerId(Long customerId);
    List<OrderResponse> getByVehicleId(Long vehicleId);
    OrderResponse update(Long id, OrderRequest request);
    void delete(Long id);
}