package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.entity.*;
import com.swp391.evdealersystem.enums.*;
import com.swp391.evdealersystem.repository.CustomerRepository;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final CustomerRepository customerRepo;
    private final ElectricVehicleRepository vehicleRepo;

    public Order toEntity(OrderRequest req) {
        Customer customer = customerRepo.findById(req.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        ElectricVehicle vehicle = vehicleRepo.findById(req.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));


        return Order.builder()
                .customer(customer)
                .vehicle(vehicle)
                .orderDate(LocalDateTime.now())
                .status(req.getStatus() != null ? req.getStatus() : OrderStatus.NEW)
                .totalAmount(req.getTotalAmount())
                .depositAmount(req.getDepositAmount())
                .deliveryDate(req.getDeliveryDate())
                .paymentStatus(OrderPaymentStatus.UNPAID)
                .currency("VND")
                .build();
    }

    public OrderResponse toResponse(Order entity) {
        if (entity == null) return null;
        return OrderResponse.builder()
                .orderId(entity.getOrderId())
                .customerId(entity.getCustomer() != null ? entity.getCustomer().getCustomerId() : null)
                .customerName(entity.getCustomer() != null ? entity.getCustomer().getName() : null)
                .vehicleId(entity.getVehicle() != null ? entity.getVehicle().getVehicleId() : null)
                .vehicleModel(entity.getVehicle() != null ? entity.getVehicle().getModel().getModelCode() : null)
                .totalAmount(entity.getTotalAmount())
                .depositAmount(entity.getDepositAmount())
                .status(entity.getStatus())
                .paymentStatus(entity.getPaymentStatus())
                .deliveryDate(entity.getDeliveryDate())
                .orderDate(entity.getOrderDate())
                .build();
    }

    public void updateEntity(Order entity, OrderRequest req) {
        if (req.getTotalAmount() != null) entity.setTotalAmount(req.getTotalAmount());
        if (req.getDepositAmount() != null) entity.setDepositAmount(req.getDepositAmount());
        if (req.getStatus() != null) entity.setStatus(req.getStatus());
        if (req.getDeliveryDate() != null) entity.setDeliveryDate(req.getDeliveryDate());
    }
}
