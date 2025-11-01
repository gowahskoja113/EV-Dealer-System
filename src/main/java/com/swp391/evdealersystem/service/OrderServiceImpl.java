package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.OrderDepositRequest;
import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.response.OrderDepositResponse;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.enums.OrderPaymentStatus;
import com.swp391.evdealersystem.enums.OrderStatus;
import com.swp391.evdealersystem.mapper.OrderMapper;
import com.swp391.evdealersystem.repository.CustomerRepository;
import com.swp391.evdealersystem.repository.ElectricVehicleRepository;
import com.swp391.evdealersystem.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final ElectricVehicleRepository vehicleRepo;
    private final OrderMapper mapper;

    private void validateDeposit(ElectricVehicle v, java.math.BigDecimal deposit) {
        if (v.getPrice() == null || v.getPrice().signum() < 0) {
            throw new IllegalArgumentException("vehicle.price must be >= 0");
        }
        if (deposit == null || deposit.signum() < 0 || deposit.compareTo(v.getPrice()) > 0) {
            throw new IllegalArgumentException("depositAmount must be between 0 and vehicle.price");
        }
    }

    // ===== BƯỚC 1: Tạo hợp đồng đặt cọc =====
    @Transactional
    @Override
    public OrderDepositResponse createDepositOrder(OrderDepositRequest req) {
        Customer customer = customerRepo.findById(req.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + req.getCustomerId()));
        ElectricVehicle vehicle = vehicleRepo.findById(req.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + req.getVehicleId()));

        validateDeposit(vehicle, req.getDepositAmount());

        // Luôn để UNPAID; sẽ chuyển sang DEPOSIT_PAID sau khi thanh toán thành công
        Order order = Order.builder()
                .customer(customer)
                .vehicle(vehicle)
                .orderDate(req.getOrderDate())   // null -> @PrePersist set now
                .status(OrderStatus.PROCESSING)  // sync theo paymentStatus
                .depositAmount(req.getDepositAmount())
                .paymentStatus(OrderPaymentStatus.UNPAID)
                .currency("VND")
                .build();

        order = orderRepo.save(order);
        return mapper.toDepositResponse(order);
    }
    // ===== BƯỚC 2: Thanh toán phần còn lại =====
    @Transactional
    @Override
    public OrderResponse payRemaining(Long orderId, OrderRequest req) {
        Order order = orderRepo.findGraphByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        // Nếu không truyền -> mặc định PAID
        OrderPaymentStatus next = (req.getPaymentStatus() != null)
                ? req.getPaymentStatus()
                : OrderPaymentStatus.PAID;
        order.setPaymentStatus(next);

        if (req.getDeliveryDate() != null) {
            order.setDeliveryDate(req.getDeliveryDate());
        }

        order = orderRepo.save(order); // @PreUpdate sync remaining + status
        return mapper.toOrderResponse(order);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
        orderRepo.delete(order);
    }

    @Transactional
    @Override
    public OrderResponse getById(Long id) {
        Order order = orderRepo.findGraphByOrderId(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
        return mapper.toOrderResponse(order);
    }

    @Transactional
    @Override
    public List<OrderResponse> getAll() {
        return orderRepo.findAll().stream()
                .map(mapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<OrderResponse> getByCustomerId(Long customerId) {
        return orderRepo.findByCustomer_CustomerId(customerId).stream()
                .map(mapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<OrderResponse> getByVehicleId(Long vehicleId) {
        return orderRepo.findByVehicle_VehicleId(vehicleId).stream()
                .map(mapper::toOrderResponse)
                .collect(Collectors.toList());
    }
}
