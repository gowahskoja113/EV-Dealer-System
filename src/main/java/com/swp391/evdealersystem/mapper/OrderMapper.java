package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.response.OrderDepositResponse;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toOrderResponse(Order entity) {
        if (entity == null) return null;
        return new OrderResponse(entity);
    }

    public OrderDepositResponse toDepositResponse(Order o) {
        if (o == null) return null;

        ElectricVehicle vehicle = (o.getSerial() != null) ? o.getSerial().getVehicle() : null;

        OrderDepositResponse res = new OrderDepositResponse();
        res.setOrderId(o.getOrderId());
        res.setCustomerId(o.getCustomer() != null ? o.getCustomer().getCustomerId() : null);
        res.setVehicleId(vehicle != null ? vehicle.getVehicleId() : null);
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