package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.OrderDepositRequest;
import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.response.OrderDepositResponse;
import com.swp391.evdealersystem.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    // Bước 1
    OrderDepositResponse createDepositOrder(OrderDepositRequest req);

    // Bước 2
    OrderResponse payRemaining(Long orderId, OrderRequest req);

    // Read APIs
    OrderResponse getById(Long id);
    List<OrderResponse> getAll();
    List<OrderResponse> getByCustomerId(Long customerId);
    List<OrderResponse> getByVehicleId(Long vehicleId);

    // Delete nếu cần
    void delete(Long id);
}
