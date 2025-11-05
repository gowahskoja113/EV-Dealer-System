package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.response.DepositOrderView;
import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderRepository orderRepo;

    @Override
    @Transactional
    public List<DepositOrderView> getDepositedOrders(Long customerId, Long orderId) {
        var orders = orderRepo.findDepositedOrdersByCustomer(customerId, orderId);
        if (orderId != null && orders.isEmpty()) {
            throw new EntityNotFoundException("No deposited order found for orderId=" + orderId
                    + " and customerId=" + customerId);
        }
        return orders.stream().map(this::toView).toList();
    }

    private DepositOrderView toView(Order o) {
        var v = new DepositOrderView();
        v.setOrderId(o.getOrderId());
        v.setOrderDate(o.getOrderDate());
        v.setCurrency(o.getCurrency());
        v.setDeliveryDate(o.getDeliveryDate());

        if (o.getCustomer() != null) {
            v.setCustomerId(o.getCustomer().getCustomerId());
            v.setCustomerName(o.getCustomer().getName());
        }
        if (o.getSerial() != null) {
            v.setVin(o.getSerial().getVin());
            if (o.getSerial().getVehicle() != null) {
                var veh = o.getSerial().getVehicle();
                v.setVehicleId(veh.getVehicleId());
                v.setPrice(veh.getPrice());
                if (veh.getModel() != null) {
                    // tuỳ schema: brand hay modelName
                    v.setVehicleModel(veh.getModel().getBrand());
                }
            }
        }

        v.setDepositAmount(o.getDepositAmount());
        v.setRemainingAmount(o.getRemainingAmount());
        v.setPaymentStatus(o.getPaymentStatus());
        v.setStatus(o.getStatus());
        return v;
    }
}
