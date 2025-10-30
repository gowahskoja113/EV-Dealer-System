package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.response.OrderDepositResponse;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    /** Dùng cho list/detail: trả OrderResponse (totalAmount = vehicle.price) */
    public OrderResponse toOrderResponse(Order entity) {
        if (entity == null) return null;

        return OrderResponse.builder()
                .orderId(entity.getOrderId())
                .customerId(entity.getCustomer() != null ? entity.getCustomer().getCustomerId() : null)
                .customerName(entity.getCustomer() != null ? entity.getCustomer().getName() : null)
                .vehicleId(entity.getVehicle() != null ? entity.getVehicle().getVehicleId() : null)
                .vehicleModel(entity.getVehicle() != null && entity.getVehicle().getModel() != null
                        ? entity.getVehicle().getModel().getModelCode() : null)
                .totalAmount(entity.getVehicle() != null ? entity.getVehicle().getPrice() : null)
                .depositAmount(entity.getDepositAmount())
                .status(entity.getStatus())
                .paymentStatus(entity.getPaymentStatus())
                .deliveryDate(entity.getDeliveryDate())
                .orderDate(entity.getOrderDate())
                .build();
    }

    /** Dùng cho bước 1: trả thông tin đặt cọc & remaining */
    public OrderDepositResponse toDepositResponse(Order o) {
        if (o == null) return null;
        OrderDepositResponse res = new OrderDepositResponse();
        res.setOrderId(o.getOrderId());
        res.setCustomerId(o.getCustomer() != null ? o.getCustomer().getCustomerId() : null);
        res.setVehicleId(o.getVehicle() != null ? o.getVehicle().getVehicleId() : null);
        res.setDepositAmount(o.getDepositAmount());
        res.setRemainingAmount(o.getRemainingAmount());
        res.setPaymentStatus(o.getPaymentStatus());
        res.setStatus(o.getStatus());
        res.setCurrency(o.getCurrency());
        res.setOrderDate(o.getOrderDate());
        res.setDeliveryDate(o.getDeliveryDate());
        return res;
    }
}
