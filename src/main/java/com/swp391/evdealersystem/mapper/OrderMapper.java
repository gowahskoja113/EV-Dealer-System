package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.response.OrderDepositResponse;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderMapper {

    public OrderResponse toOrderResponse(Order order) {
        if (order == null) return null;

        OrderResponse res = new OrderResponse();
        res.setOrderId(order.getOrderId());

        if (order.getSerial() != null && order.getSerial().getVehicle() != null) {
            BigDecimal totalAmount = order.getSerial().getVehicle().getPrice();
            res.setTotalAmount(totalAmount);
            res.setVehicleId(order.getSerial().getVehicle().getVehicleId());

            if (order.getSerial().getVehicle().getModel() != null) {
                res.setVehicleModel(order.getSerial().getVehicle().getModel().getBrand());
            }
        } else {
            // Fallback if vehicle is null (rare case)
            if (order.getRemainingAmount() != null && order.getDepositAmount() != null) {
                res.setTotalAmount(order.getRemainingAmount().add(order.getDepositAmount()));
            } else {
                res.setTotalAmount(BigDecimal.ZERO);
            }
        }

        res.setPlanedDepositAmount(order.getPlannedDepositAmount());
        res.setRemainingAmount(order.getRemainingAmount());
        res.setStatus(order.getStatus());
        res.setPaymentStatus(order.getPaymentStatus());
        res.setDeliveryDate(order.getDeliveryDate());
        res.setOrderDate(order.getOrderDate()); // Lấy ngày đặt hàng

        if (order.getCustomer() != null) {
            res.setCustomerId(order.getCustomer().getCustomerId());
            res.setCustomerName(order.getCustomer().getName());
        }

        return res;
    }

    public OrderDepositResponse toDepositResponse(Order o) {
        if (o == null) return null;

        ElectricVehicle vehicle = (o.getSerial() != null) ? o.getSerial().getVehicle() : null;

        OrderDepositResponse res = new OrderDepositResponse();
        res.setOrderId(o.getOrderId());
        res.setCustomerId(o.getCustomer() != null ? o.getCustomer().getCustomerId() : null);
        res.setVehicleId(vehicle != null ? vehicle.getVehicleId() : null);
        res.setDepositAmount(o.getPlannedDepositAmount());
        res.setRemainingAmount(o.getRemainingAmount());
        res.setPaymentStatus(o.getPaymentStatus());
        res.setStatus(o.getStatus());
        res.setCurrency(o.getCurrency());
        res.setOrderDate(o.getOrderDate());
        res.setDeliveryDate(o.getDeliveryDate());
        return res;
    }
}