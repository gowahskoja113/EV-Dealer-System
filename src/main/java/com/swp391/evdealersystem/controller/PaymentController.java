package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.response.PaymentResponse;
import com.swp391.evdealersystem.entity.Payment;
import com.swp391.evdealersystem.enums.PaymentPurpose;
import com.swp391.evdealersystem.mapper.PaymentMapper;
import com.swp391.evdealersystem.repository.PaymentRepository;
import com.swp391.evdealersystem.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepo;
    private final PaymentMapper paymentMapper;

    // Tạo URL thanh toán VNPay cho CỌC / PHẦN CÒN LẠI
    @PostMapping("/vnpay/create")
    public ResponseEntity<?> createVnPayPayment(
            @RequestParam Long orderId,
            @RequestParam PaymentPurpose purpose, // DEPOSIT|BALANCE
            @RequestParam BigDecimal amount,
            HttpServletRequest request) {

        String clientIp = getClientIp(request);
        Payment p = paymentService.createVnPayPayment(orderId, purpose, amount, clientIp);
        return ResponseEntity.ok(Map.of(
                "paymentId", p.getPaymentId(),
                "vnpTxnRef", p.getVnpTxnRef(),
                "payUrl", p.getPayUrl(),
                "status", p.getStatus().name()
        ));
    }

    // VNPay redirect về — bạn có thể trả HTML/redirect FE; ở đây trả text
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

    @GetMapping("/orders/{orderId}/payments")
    public ResponseEntity<List<PaymentResponse>> listPaymentsByOrder(@PathVariable Long orderId) {
        List<PaymentResponse> res = paymentRepo.findAll().stream() // => nên viết repo method findByOrder_OrderId(orderId) để filter
                .filter(p -> p.getOrder() != null && orderId.equals(p.getOrder().getOrderId()))
                .map(paymentMapper::toResponse)
                .toList();
        return ResponseEntity.ok(res);
    }


    private String getClientIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = req.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip;
    }

    @PostMapping("/cash")
    public ResponseEntity<?> createCashPayment(
            @RequestParam Long orderId,
            @RequestParam PaymentPurpose purpose,
            @RequestParam BigDecimal amount) {

        Payment p = paymentService.createCashPayment(orderId, purpose, amount);
        return ResponseEntity.ok(Map.of(
                "paymentId", p.getPaymentId(),
                "method", p.getPaymentMethod(),
                "status", p.getStatus(),
                "purpose", p.getPurpose(),
                "amount", p.getAmount()
        ));
    }

}
