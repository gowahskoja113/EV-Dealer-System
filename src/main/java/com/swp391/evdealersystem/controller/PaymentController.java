package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.CashPaymentRequest;
import com.swp391.evdealersystem.dto.request.StartVnpayRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.dto.response.PaymentHistoryResponse;
import com.swp391.evdealersystem.dto.response.StartVnpayResponse;
import com.swp391.evdealersystem.dto.response.VnpIpnResponse;
import com.swp391.evdealersystem.service.PaymentService;
import com.swp391.evdealersystem.service.PaymentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/cash/{orderId}")
    public ResponseEntity<OrderResponse> payCash(
            @PathVariable Long orderId,
            @Valid @RequestBody CashPaymentRequest req
    ) {
        return ResponseEntity.ok(paymentService.processCash(orderId, req));
    }

    @PostMapping("/vnpay/start/{orderId}")
    public ResponseEntity<StartVnpayResponse> startVnpay(
            @PathVariable Long orderId,
            @Valid @RequestBody StartVnpayRequest req
    ) {
        return ResponseEntity.ok(paymentService.startVnpay(orderId, req));
    }

    @GetMapping("/vnpay/return")
    public ResponseEntity<VnpIpnResponse> vnpayReturn(
            @RequestParam Map<String,String> params
    ) {
        return ResponseEntity.ok(paymentService.processVnpayCallback(params));
    }

    @GetMapping("/vnpay/ipn")
    public ResponseEntity<VnpIpnResponse> vnpayIpn(
            @RequestParam Map<String,String> params
    ) {
        return ResponseEntity.ok(paymentService.processVnpayCallback(params));
    }

    @GetMapping
    public ResponseEntity<Page<PaymentHistoryResponse>> getPaymentHistory(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) java.time.LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(paymentService.getPayments(
                customerId,
                from,
                to,
                PageRequest.of(page, size)
        ));
    }
}

