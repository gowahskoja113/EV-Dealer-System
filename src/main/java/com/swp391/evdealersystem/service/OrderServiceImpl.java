package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.OrderDepositRequest;
import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.request.UpdateDeliveryDateRequest;
import com.swp391.evdealersystem.dto.response.DeliverySlipDTO;
import com.swp391.evdealersystem.dto.response.OrderDepositResponse;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.entity.*;
import com.swp391.evdealersystem.enums.OrderPaymentStatus;

import java.io.IOException;
import java.util.stream.Collectors;

import com.swp391.evdealersystem.enums.OrderStatus;
import com.swp391.evdealersystem.enums.VehicleStatus;
import com.swp391.evdealersystem.mapper.OrderMapper;
import com.swp391.evdealersystem.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final VehicleSerialRepository serialRepo;
    private final OrderMapper mapper;
    private final WarehouseStockRepository stockRepo;
    private final WarehouseRepository warehouseRepo;
    private final PdfGenerationService pdfGenerationService;

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

        VehicleSerial serial = serialRepo.findByVinForUpdate(req.getVin())
                .orElseThrow(() -> new IllegalArgumentException("VehicleSerial not found with VIN: " + req.getVin()));

        if (orderRepo.existsBySerial_VinAndStatus(serial.getVin(), com.swp391.evdealersystem.enums.OrderStatus.PROCESSING)) {
            throw new IllegalStateException("This vehicle is already reserved by another processing order.");
        }
        if (!serial.isSelectableNow()) {
            throw new IllegalStateException("Vehicle " + serial.getVin() + " is not available (Status: " + serial.getStatus() + ")");
        }

        validateDeposit(serial.getVehicle(), req.getDepositAmount());

        serial.setStatus(VehicleStatus.HOLD);
        serial.setHoldUntil(OffsetDateTime.now().plusDays(30));

        Order order = Order.builder()
                .customer(customer)
                .serial(serial)
                .orderDate(req.getOrderDate())
                .currency("VND")
                .status(com.swp391.evdealersystem.enums.OrderStatus.PROCESSING)
                .paymentStatus(OrderPaymentStatus.UNPAID)
                .plannedDepositAmount(req.getDepositAmount())
                .depositAmount(java.math.BigDecimal.ZERO)
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

        if (req.getDeliveryDate() != null) {
            order.setDeliveryDate(req.getDeliveryDate());
        }

        VehicleSerial serial = order.getSerial();
        if (serial != null) {
            switch (next) {
                case PAID -> {
                    var price = serial.getVehicle().getPrice();
                    order.setDepositAmount(price);

                    if (serial.getStatus() != VehicleStatus.SOLD_OUT) {
                        serial.setStatus(VehicleStatus.SOLD_OUT);
                        serial.setHoldUntil(null);
                        serialRepo.save(serial);

                        Long whId = serial.getWarehouse().getWarehouseId();
                        Long modelId = serial.getModel().getModelId();

                        var stock = stockRepo.findForUpdate(whId, modelId)
                                .orElseThrow(() -> new IllegalStateException("Stock not found for warehouse/model"));
                        if (stock.getQuantity() <= 0) {
                            throw new IllegalStateException("Stock would go negative for model " + serial.getModel().getModelCode());
                        }
                        stock.setQuantity(stock.getQuantity() - 1);
                        stockRepo.save(stock);

                        int total = stockRepo.sumQuantityByWarehouseId(whId);
                        var wh = serial.getWarehouse();
                        wh.setVehicleQuantity(total);
                        warehouseRepo.save(wh);
                    }
                    order.setStatus(com.swp391.evdealersystem.enums.OrderStatus.COMPLETED);
                }
                case OVERDUE -> {
                    if (serial.getStatus() != VehicleStatus.SOLD_OUT) {
                        serial.setStatus(VehicleStatus.AVAILABLE);
                        serial.setHoldUntil(null);
                        serialRepo.save(serial);
                    }
                    order.setStatus(OrderStatus.CANCELED);
                }
                default -> { /* không đổi gì với VIN */ }
            }
        }

        order.setPaymentStatus(next); // set sau khi tinh deposit
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
                .map(mapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public OrderResponse setDeliveryDate(Long orderId, UpdateDeliveryDateRequest request) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        if (order.getPaymentStatus() != OrderPaymentStatus.PAID) {
            throw new IllegalStateException("Order must be fully PAID before setting delivery date.");
        }

        order.setDeliveryDate(request.getDeliveryDate());
        order = orderRepo.save(order);

        return mapper.toOrderResponse(order);
    }
    @Transactional
    @Override
    public byte[] generateDeliverySlip(Long orderId) {
        Order order = orderRepo.findOrderDetailsForContract(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with full details: " + orderId));

        if (order.getDeliveryDate() == null) {
            throw new IllegalStateException("Delivery date must be set before generating delivery slip.");
        }

        DeliverySlipDTO dto = mapOrderToDeliverySlipDTO(order);

        try {
            return pdfGenerationService.generateDeliverySlipPdf(dto);
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF delivery slip: " + e.getMessage(), e);
        }
    }

    private DeliverySlipDTO mapOrderToDeliverySlipDTO(Order order) {
        Customer c = order.getCustomer();
        VehicleSerial s = order.getSerial();
        Model m = s.getVehicle().getModel();
        User sales = c.getAssignedSales();

        return DeliverySlipDTO.builder()
                .orderId(order.getOrderId())
                .deliveryDate(order.getDeliveryDate())
                .salespersonName(sales != null ? sales.getName() : "N/A (Demo)") // Giả sử User có getFullName()
                .customerName(c.getName())
                .customerAddress(c.getAddress())
                .customerPhone(c.getPhoneNumber())
                .vehicleBrand(m.getBrand())
                .vehicleModelCode(m.getModelCode())
                .vehicleColor(m.getColor())
                .vehicleVin(s.getVin())
                .build();
    }
}