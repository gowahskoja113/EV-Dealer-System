package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.enums.OrderPaymentStatus;
import com.swp391.evdealersystem.enums.OrderStatus;
import com.swp391.evdealersystem.mapper.OrderMapper;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import com.swp391.evdealersystem.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final ElectricVehicleRepository vehicleRepo;
    private final OrderMapper orderMapper;

    private void validateAmounts(BigDecimal total, BigDecimal deposit) {
        if (total == null || total.signum() < 0) {
            throw new IllegalArgumentException("totalAmount must be >= 0");
        }
        if (deposit != null && (deposit.signum() < 0 || deposit.compareTo(total) > 0)) {
            throw new IllegalArgumentException("depositAmount must be between 0 and totalAmount");
        }
    }

    private void ensureDefaultStatus(Order order) {
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.NEW);
        }
        if (order.getPaymentStatus() == null) {
            order.setPaymentStatus(OrderPaymentStatus.UNPAID);
        }
        if (order.getCurrency() == null) {
            order.setCurrency("VND");
        }
    }

    @Transactional
    @Override
    public OrderResponse create(OrderRequest request) {
        Order order = orderMapper.toEntity(request);

        // neu khong set totalAmount, lay tu vehicle cost
        if (order.getTotalAmount() == null) {
            ElectricVehicle vehicle = order.getVehicle();
            if (vehicle == null) {
                throw new IllegalArgumentException("Vehicle is required");
            }

            order.setTotalAmount(vehicle.getCost());
        }

        ensureDefaultStatus(order);
        validateAmounts(order.getTotalAmount(), order.getDepositAmount());

        order = orderRepo.save(order);
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse getById(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> getAll() {
        return orderRepo.findAll()
                .stream().map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> getByCustomerId(Long customerId) {
        return orderRepo.findByCustomer_CustomerId(customerId)
                .stream().map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> getByVehicleId(Long vehicleId) {
        return orderRepo.findByVehicle_VehicleId(vehicleId)
                .stream().map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public OrderResponse update(Long id, OrderRequest request) {
        Order current = orderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));

        orderMapper.updateEntity(current, request);

        //neu depositAmount null thi giu nguyen
        ensureDefaultStatus(current);
        validateAmounts(current.getTotalAmount(), current.getDepositAmount());

        // neu tong tien null thi lay tu vehicle cost
        if (current.getTotalAmount() == null) {
            ElectricVehicle vehicle = vehicleRepo.findById(current.getVehicle().getVehicleId())
                    .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));
            current.setTotalAmount(vehicle.getCost());
        }

        Order saved = orderRepo.save(current);
        return orderMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
        orderRepo.delete(order);
    }
}
