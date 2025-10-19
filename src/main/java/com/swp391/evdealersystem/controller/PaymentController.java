package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.PaymentRequest;
import com.swp391.evdealersystem.dto.response.PaymentResponse;
import com.swp391.evdealersystem.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping(value = "/vnpay/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PaymentResponse create(@Valid @RequestBody PaymentRequest request, HttpServletRequest httpReq) {
        String clientIp = getClientIp(httpReq);
        return paymentService.createVnPayPayment(request, clientIp);
    }

    // Người dùng bị redirect về đây sau khi thanh toán
    @GetMapping("/vnpay/return")
    public PaymentResponse vnpReturn(@RequestParam Map<String, String> allParams) {
        return paymentService.handleReturn(allParams);
    }

    // VNPAY server gọi IPN tới URL này
    @GetMapping(value = "/vnpay/ipn", produces = MediaType.APPLICATION_JSON_VALUE)
    public String vnpIpn(@RequestParam Map<String, String> allParams) {
        return paymentService.handleIpn(allParams);
    }

    @GetMapping("/{id}")
    public PaymentResponse get(@PathVariable Long id) {
        return paymentService.getById(id);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) ip = request.getRemoteAddr();
        if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
        return ip;
    }
}