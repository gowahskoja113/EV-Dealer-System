package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.service.VNPAYService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Data
@RestController
@RequestMapping("/api/vnpay")
public class VnpayController {

    private final VNPAYService vnpayService;

    public VnpayController(VNPAYService vnpayService) {
        this.vnpayService = vnpayService;
    }

    @PostMapping("/create")
    public Map<String, String> createPayment(@RequestParam long amount,
                                             @RequestParam(required = false) String bankCode) throws Exception {
        String paymentUrl = vnpayService.createPaymentUrl(amount, bankCode, "Thanh toan don hang demo");
        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        return response;
    }

    // VNPay callback (Return URL)
    @GetMapping("/vnpay_return")

    public String vnpayReturn(@RequestParam Map<String, String> params) {
        // TODO: xác minh SecureHash, kiểm tra trạng thái thanh toán
        return "VNPay response: " + params.toString();
    }
}