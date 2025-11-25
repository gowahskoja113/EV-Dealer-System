package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.OrderDepositRequest;
import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.request.UpdateDeliveryDateRequest;
import com.swp391.evdealersystem.dto.response.CustomerWithOrdersResponse;
import com.swp391.evdealersystem.dto.response.DeliverySlipDTO;
import com.swp391.evdealersystem.dto.response.OrderDepositResponse;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.entity.*;
import com.swp391.evdealersystem.enums.OrderPaymentStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

import com.swp391.evdealersystem.enums.OrderStatus;
import com.swp391.evdealersystem.enums.VehicleStatus;
import com.swp391.evdealersystem.mapper.OrderMapper;
import com.swp391.evdealersystem.repository.*;
import com.swp391.evdealersystem.util.AuthenticationHelper;
import com.swp391.evdealersystem.util.BusinessValidationUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRepository userRepository;
    private final AuthenticationHelper authenticationHelper;

    @Transactional
    @Override
    public OrderDepositResponse createDepositOrder(OrderDepositRequest req) {
        LocalDateTime now = LocalDateTime.now();
        User salesPerson = authenticationHelper.getCurrentUser();

        Customer customer = customerRepo.findById(req.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + req.getCustomerId()));

        VehicleSerial serial = serialRepo.findByVinForUpdate(req.getVin())
                .orElseThrow(() -> new IllegalArgumentException("VehicleSerial not found with VIN: " + req.getVin()));

        // --- 1. BẢO MẬT: Check quyền Dealer ---
        if (!"ROLE_ADMIN".equals(salesPerson.getRole().getRoleName())) {
            if (salesPerson.getDealership() == null) {
                throw new AccessDeniedException("Nhân viên chưa thuộc Đại lý nào.");
            }
            Long salesDealerId = salesPerson.getDealership().getDealershipId();
            Long vehicleDealerId = serial.getWarehouse().getDealership().getDealershipId();

            if (!salesDealerId.equals(vehicleDealerId)) {
                throw new AccessDeniedException(
                        "CHẶN ĐỨNG: Bạn thuộc Dealer " + salesDealerId +
                                " nhưng đang cố bán xe thuộc Dealer " + vehicleDealerId
                );
            }
        }

        if (orderRepo.existsBySerial_VinAndStatus(serial.getVin(), OrderStatus.PROCESSING)) {
            throw new IllegalStateException("Xe đang được xử lý ở đơn hàng khác.");
        }
        if (!serial.isSelectableNow()) {
            throw new IllegalStateException("Xe không khả dụng (Status: " + serial.getStatus() + ")");
        }

        BusinessValidationUtils.validateDeposit(serial.getVehicle(), req.getDepositAmount());

        // --- 3. GIỮ XE (HOLD) ---
        serial.setStatus(VehicleStatus.HOLD);
        serial.setHoldUntil(now.plusDays(14).atOffset(OffsetDateTime.now().getOffset()));

        // --- 4. TẠO ORDER ---
        Order order = Order.builder()
                .customer(customer)
                .serial(serial)
                .orderDate(req.getOrderDate() != null ? req.getOrderDate() : now)
                .currency("VND")
                .status(OrderStatus.PROCESSING)
                .paymentStatus(OrderPaymentStatus.UNPAID)
                .plannedDepositAmount(req.getDepositAmount())
                .depositAmount(BigDecimal.ZERO)
                .build();

        return mapper.toDepositResponse(orderRepo.save(order));
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
                    order.setStatus(OrderStatus.COMPLETED);
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

        order.setPaymentStatus(next);
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

    @Override
    public CustomerWithOrdersResponse getCustomerWithOrdersById(Long customerId) {
        return null;
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

        if (order.getStatus() != OrderStatus.ORDER_PAID && order.getStatus() != OrderStatus.DELIVERING) {
            throw new IllegalStateException("Đơn hàng phải ở trạng thái ORDER_PAID hoặc DELIVERING mới được set ngày giao.");
        }

        if (request.getDeliveryDate() == null) {
            throw new IllegalArgumentException("Ngày giao hàng không được rỗng.");
        }

        LocalDate requestedDate = request.getDeliveryDate();
        LocalDate today = LocalDate.now();

        if (requestedDate.isBefore(today)) {
            throw new IllegalArgumentException("Ngày giao hàng không thể ở trong quá khứ.");
        }

        // 1. Update Order
        order.setDeliveryDate(requestedDate);
        order.setStatus(OrderStatus.DELIVERING);

        // 2. Update Vehicle -> DELIVERING (Đang giao)
        VehicleSerial serial = order.getSerial();
        if (serial != null) {
            // Chỉ update nếu xe chưa phải là DELIVERED
            if (serial.getStatus() != VehicleStatus.DELIVERED) {
                serial.setStatus(VehicleStatus.DELIVERING);
                serialRepo.save(serial);
            }
        }

        order = orderRepo.save(order);
        return mapper.toOrderResponse(order);
    }

    @Transactional
    @Override
    public OrderResponse deliverOrderNow(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found: " + orderId));

        if (order.getPaymentStatus() != OrderPaymentStatus.PAID) {
            throw new IllegalStateException("Đơn hàng chưa thanh toán đủ, không thể giao xe.");
        }

        if (order.getStatus() != OrderStatus.ORDER_PAID &&
                order.getStatus() != OrderStatus.DELIVERING &&
                order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Trạng thái không hợp lệ. Status: " + order.getStatus());
        }

        // 1. Update Order -> COMPLETED
        order.setDeliveryDate(LocalDate.now());
        order.setStatus(OrderStatus.COMPLETED);

        // 2. Update Vehicle -> DELIVERED (Đã giao)
        VehicleSerial serial = order.getSerial();
        if (serial != null) {
            serial.setStatus(VehicleStatus.DELIVERED);
            serial.setHoldUntil(null); // Đảm bảo xóa hold (dù logic trước đó đã xóa rồi nhưng cứ chắc chắn)
            serialRepo.save(serial);
        }

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

        if (order.getStatus() == OrderStatus.DELIVERING &&
                !order.getDeliveryDate().isAfter(LocalDate.now())) {

            order.setStatus(OrderStatus.COMPLETED);
            order = orderRepo.save(order);
        }

        if (order.getStatus() != OrderStatus.DELIVERING && order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Order is not ready for delivery slip. Status: " + order.getStatus());
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
                .salespersonName(sales != null ? sales.getName() : "N/A")
                .customerName(c.getName())
                .customerAddress(c.getAddress())
                .customerPhone(c.getPhoneNumber())
                .vehicleBrand(m.getBrand())
                .vehicleModelCode(m.getModelCode())
                .vehicleColor(m.getColor())
                .vehicleVin(s.getVin())
                .vehiclePrice(s.getVehicle().getPrice())
                .amountPaid(order.getDepositAmount())
                .remainingAmount(order.getRemainingAmount())
                .build();
    }
}