package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Order;

public class OrderMapper {

    public static Order toEntity(OrderRequest req, Customer customer, ElectricVehicle vehicle) {
        Order o = new Order();
        o.setCustomer(customer);
        o.setVehicle(vehicle);
        o.setOrderDate(req.getOrderDate());
        o.setStatus(req.getStatus());
        return o;
    }

    public static void updateEntity(Order o, OrderRequest req, Customer customer, ElectricVehicle vehicle) {
        o.setCustomer(customer);
        o.setVehicle(vehicle);
        o.setOrderDate(req.getOrderDate());
        o.setStatus(req.getStatus());
    }

    public static OrderResponse toResponse(Order o) {
        return OrderResponse.builder()
                .orderId(o.getOrderId())
                .customerId(o.getCustomer().getCustomerId())
                .customerName(o.getCustomer().getName())
                .customerPhone(o.getCustomer().getPhoneNumber())
                .vehicleId(o.getVehicle().getVehicleId())
                .vehicleModelId(o.getVehicle().getModel().getModelId())
                .vehicleModelCode(o.getVehicle().getModel().getModelCode())
                .orderDate(o.getOrderDate())
                .status(o.getStatus())
                .build();
    }
}