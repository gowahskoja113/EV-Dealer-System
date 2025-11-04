package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.OrderDepositRequest;
import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.response.OrderDepositResponse;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.entity.VehicleSerial;
import com.swp391.evdealersystem.enums.OrderPaymentStatus;
import java.util.stream.Collectors;
import com.swp391.evdealersystem.enums.VehicleStatus;
import com.swp391.evdealersystem.mapper.OrderMapper;
import com.swp391.evdealersystem.repository.CustomerRepository;
import com.swp391.evdealersystem.repository.OrderRepository;
import com.swp391.evdealersystem.repository.VehicleSerialRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final VehicleSerialRepository serialRepo;
    private final OrderMapper mapper;

    private void validateDeposit(ElectricVehicle v, java.math.BigDecimal deposit) {
        if (v.getPrice() == null || v.getPrice().signum() < 0) {
            throw new IllegalArgumentException("vehicle.price must be >= 0");
        }
            if (deposit == null || deposit.signum() < 0 || deposit.compareTo(v.getPrice()) > 0) {
                throw new IllegalArgumentException("depositAmount must be between 0 and vehicle.price");
            }
    }

    @Transactional
    @Override
    public OrderDepositResponse createDepositOrder(OrderDepositRequest req) {
        Customer customer = customerRepo.findById(req.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + req.getCustomerId()));

        VehicleSerial serial = serialRepo.findById(req.getVehicleSerialId())
                .orElseThrow(() -> new IllegalArgumentException("VehicleSerial not found: " + req.getVehicleSerialId()));

        if (orderRepo.existsBySerial_VinAndStatus(serial.getVin(), com.swp391.evdealersystem.enums.OrderStatus.PROCESSING)) {
            throw new IllegalStateException("This vehicle is already reserved by another processing order.");
        }

        if (!serial.isSelectableNow()) {
            throw new IllegalStateException("Vehicle " + serial.getVin() + " is not available (Status: " + serial.getStatus() + ")");
        }
        validateDeposit(serial.getVehicle(), req.getDepositAmount());

        serial.setStatus(VehicleStatus.HOLD);
        serial.setHoldUntil(OffsetDateTime.now().plus(7, ChronoUnit.DAYS));
        serialRepo.save(serial);

        Order order = Order.builder()
                .customer(customer)
                .serial(serial)
                .orderDate(req.getOrderDate())
                .depositAmount(req.getDepositAmount())
                .currency("VND")
                .build();

        order = orderRepo.save(order);
        return mapper.toDepositResponse(order);
    }

    @Transactional
    @Override
    public OrderResponse payRemaining(Long orderId, OrderRequest req) {
        Order order = orderRepo.findGraphByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        OrderPaymentStatus next = (req.getPaymentStatus() != null)
                ? req.getPaymentStatus()
                : OrderPaymentStatus.PAID;
        order.setPaymentStatus(next);

        if (req.getDeliveryDate() != null) {
            order.setDeliveryDate(req.getDeliveryDate());
        }

        //update status vehicle
        VehicleSerial serial = order.getSerial();
        if (serial != null) {
            switch (next) {
                case PAID -> {
                    serial.setStatus(VehicleStatus.SOLD_OUT);
                    serial.setHoldUntil(null);
                    serialRepo.save(serial);
                }
                case OVERDUE -> {
                    serial.setStatus(VehicleStatus.AVAILABLE);
                    serial.setHoldUntil(null);
                    serialRepo.save(serial);
                }
                default -> { /* khong thay doi trang thai xe khi dang processing */ }
            }
        }
        order = orderRepo.save(order);
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
        return orderRepo.findOrdersByVehicleIdWithGraph(vehicleId).stream()
                .map(order -> new OrderResponse(order))
                .collect(Collectors.toList());
    }
}