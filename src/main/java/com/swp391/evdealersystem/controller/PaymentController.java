package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.CashPaymentRequest;
import com.swp391.evdealersystem.dto.request.StartVnpayRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.dto.response.StartVnpayResponse;
import com.swp391.evdealersystem.dto.response.VnpIpnResponse;
import com.swp391.evdealersystem.service.PaymentService;
import com.swp391.evdealersystem.service.PaymentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    // Return (browser)
    @GetMapping("/vnpay/return")
    public ResponseEntity<VnpIpnResponse> vnpayReturn(
            @RequestParam Map<String,String> params
    ) {
        return ResponseEntity.ok(paymentService.processVnpayCallback(params));
    }

    // IPN (server-to-server)
    @GetMapping("/vnpay/ipn")
    public ResponseEntity<VnpIpnResponse> vnpayIpn(
            @RequestParam Map<String,String> params
    ) {
        return ResponseEntity.ok(paymentService.processVnpayCallback(params));
    }
}

