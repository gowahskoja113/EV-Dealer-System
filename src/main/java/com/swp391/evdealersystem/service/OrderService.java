package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.OrderDepositRequest;
import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.request.UpdateDeliveryDateRequest;
import com.swp391.evdealersystem.dto.response.OrderDepositResponse;
import com.swp391.evdealersystem.dto.response.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderDepositResponse createDepositOrder(OrderDepositRequest req);

    OrderResponse payRemaining(Long orderId, OrderRequest req);

    OrderResponse getById(Long id);
    List<OrderResponse> getAll();
    List<OrderResponse> getByCustomerId(Long customerId);
    List<OrderResponse> getByVehicleId(Long vehicleId);

    OrderResponse setDeliveryDate(Long orderId, UpdateDeliveryDateRequest request);
    byte[] generateDeliverySlip(Long orderId);
    void delete(Long id);
}