package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.mapper.OrderMapper;
import com.swp391.evdealersystem.repository.CustomerRepository;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import com.swp391.evdealersystem.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final ElectricVehicleRepository vehicleRepo;

    @Override
    public OrderResponse create(OrderRequest request) {
        Customer customer = customerRepo.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + request.getCustomerId()));
        ElectricVehicle vehicle = vehicleRepo.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + request.getVehicleId()));

        if (request.getOrderDate() == null) {
            request.setOrderDate(LocalDateTime.now());
        }

        Order order = OrderMapper.toEntity(request, customer, vehicle);
        order = orderRepo.save(order);
        return OrderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        return OrderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        return orderRepo.findAll().stream().map(OrderMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getByCustomerId(Long customerId) {
        return orderRepo.findByCustomer_CustomerId(customerId).stream().map(OrderMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getByVehicleId(Long vehicleId) {
        return orderRepo.findByVehicle_VehicleId(vehicleId).stream().map(OrderMapper::toResponse).toList();
    }

    @Override
    public OrderResponse update(Long id, OrderRequest request) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));

        Customer customer = customerRepo.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + request.getCustomerId()));
        ElectricVehicle vehicle = vehicleRepo.findById(request.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + request.getVehicleId()));

        if (request.getOrderDate() == null) {
            request.setOrderDate(order.getOrderDate());
        }

        OrderMapper.updateEntity(order, request, customer, vehicle);
        return OrderMapper.toResponse(order);
    }

    @Override
    public void delete(Long id) {
        if (!orderRepo.existsById(id)) {
            throw new IllegalArgumentException("Order not found: " + id);
        }
        orderRepo.deleteById(id);
    }
}