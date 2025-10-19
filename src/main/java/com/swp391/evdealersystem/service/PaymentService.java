package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.PaymentRequest;
import com.swp391.evdealersystem.dto.response.PaymentResponse;

import java.util.Map;

public interface PaymentService {
    PaymentResponse createVnPayPayment(PaymentRequest request, String clientIp);
    PaymentResponse handleReturn(Map<String, String> vnpParams);
    String handleIpn(Map<String, String> vnpParams);
    PaymentResponse getById(Long id);
}