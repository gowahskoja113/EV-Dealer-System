package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.PaymentCreateCashRequest;
import com.swp391.evdealersystem.dto.request.PaymentRequest;
import com.swp391.evdealersystem.dto.response.PaymentResponse;
import com.swp391.evdealersystem.entity.Payment;
import com.swp391.evdealersystem.mapper.PaymentMapper;
import com.swp391.evdealersystem.repository.PaymentRepository;
import com.swp391.evdealersystem.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepo;
    private final PaymentMapper mapper;

    // ================== VNPAY ==================

    // Tạo URL thanh toán VNPay (DEPOSIT | BALANCE)
    @PostMapping("/vnpay/create")
    public ResponseEntity<PaymentResponse> createVnPayPayment(
            @Valid @RequestBody PaymentRequest req,
            HttpServletRequest request
    ) {
        String clientIp = getClientIp(request);
        Payment p = paymentService.createVnPayPayment(
                req.getOrderId(), req.getPurpose(), req.getAmount(), clientIp
        );
        return ResponseEntity.ok(mapper.toResponse(p));
    }

    // VNPay redirect về
    @GetMapping("/vnpay/return")
    public ResponseEntity<String> vnpReturn(@RequestParam Map<String, String> params) {
        String msg = paymentService.handleReturn(params);
        return ResponseEntity.ok(msg);
    }

    // VNPay IPN (server-to-server)
    @GetMapping("/vnpay/ipn")
    public ResponseEntity<String> vnpIpn(@RequestParam Map<String, String> params) {
        String msg = paymentService.handleIpn(params);
        return ResponseEntity.ok(msg);
    }

    // ================== CASH ==================

    @PostMapping("/cash")
    public ResponseEntity<PaymentResponse> createCashPayment(@Valid @RequestBody PaymentCreateCashRequest req) {
        Payment p = paymentService.createCashPayment(req.getOrderId(), req.getPurpose(), req.getAmount());
        return ResponseEntity.ok(mapper.toResponse(p));
    }

    // ================== QUERY ==================

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<List<PaymentResponse>> listByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(
                paymentRepo.findByOrder_OrderIdOrderByCreatedAtDesc(orderId)
                        .stream().map(mapper::toResponse).toList()
        );
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getById(@PathVariable Long paymentId) {
        Payment p = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found: " + paymentId));
        return ResponseEntity.ok(mapper.toResponse(p));
    }

    @GetMapping("/by-txnref/{vnpTxnRef}")
    public ResponseEntity<PaymentResponse> getByTxnRef(@PathVariable String vnpTxnRef) {
        Payment p = paymentRepo.findByVnpTxnRef(vnpTxnRef)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for vnpTxnRef: " + vnpTxnRef));
        return ResponseEntity.ok(mapper.toResponse(p));
    }

    // ================== Helpers & Handlers ==================

    private String getClientIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = req.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler({ IllegalArgumentException.class, MethodArgumentTypeMismatchException.class })
    public ResponseEntity<String> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
