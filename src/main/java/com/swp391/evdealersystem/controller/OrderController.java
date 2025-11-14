package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.OrderDepositRequest;
import com.swp391.evdealersystem.dto.request.OrderRequest;
import com.swp391.evdealersystem.dto.request.UpdateDeliveryDateRequest;
import com.swp391.evdealersystem.dto.response.CustomerWithOrdersResponse;
import com.swp391.evdealersystem.dto.response.DepositOrderView;
import com.swp391.evdealersystem.dto.response.OrderDepositResponse;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.mapper.OrderMapper;
import com.swp391.evdealersystem.repository.CustomerRepository;
import com.swp391.evdealersystem.repository.OrderRepository;
import com.swp391.evdealersystem.service.OrderQueryService;
import com.swp391.evdealersystem.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderMapper mapper;


    private final CustomerRepository customerRepo ;
    private final OrderRepository orderRepo ;

    private final OrderService orderService;
    private final OrderQueryService orderQueryService;

    @PostMapping("/deposit")
    public ResponseEntity<OrderDepositResponse> createDeposit(@Valid @RequestBody OrderDepositRequest req) {
        OrderDepositResponse res = orderService.createDepositOrder(req);
        return ResponseEntity.created(URI.create("/api/orders/" + res.getOrderId())).body(res);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @PostMapping("/{orderId}/pay-remaining")
    public ResponseEntity<OrderResponse> payRemaining(@PathVariable Long orderId,
                                                      @Valid @RequestBody OrderRequest req) {
        OrderResponse res = orderService.payRemaining(orderId, req);
        return ResponseEntity.ok(res);
    }

    @GetMapping({"/deposit/{customerId}", "/deposit/{customerId}/{orderId}"})
    public ResponseEntity<List<DepositOrderView>> getDepositedOrders(
            @PathVariable Long customerId,
            @RequestParam(required = false) Long orderId
    ) {
        List<DepositOrderView> data = orderQueryService.getDepositedOrders(customerId, orderId);
        return ResponseEntity.ok(data);
    }

    @PutMapping("/{orderId}/delivery")
    public ResponseEntity<OrderResponse> updateDeliveryDate(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateDeliveryDateRequest request) {

        OrderResponse response = orderService.setDeliveryDate(orderId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/deliver-now")
    public ResponseEntity<OrderResponse> deliverNow(@PathVariable Long orderId) {
        OrderResponse response = orderService.deliverOrderNow(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}/delivery-slip")
    public ResponseEntity<byte[]> getDeliverySlip(@PathVariable Long orderId) {

        byte[] pdfBytes = orderService.generateDeliverySlip(orderId);
        String filename = "PhieuGiaoXe_" + orderId + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @Transactional

    public CustomerWithOrdersResponse getCustomerWithOrdersById(Long customerId) {
        // Lấy thông tin khách hàng
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found: " + customerId));

        // Lấy các đơn hàng của khách hàng
        List<OrderResponse> orders = orderRepo.findByCustomer_CustomerId(customerId).stream()
                .map(mapper::toOrderResponse)
                .collect(Collectors.toList());

        // Chuyển thông tin khách hàng và đơn hàng sang DTO
        return new CustomerWithOrdersResponse(customer, orders);
    }

}