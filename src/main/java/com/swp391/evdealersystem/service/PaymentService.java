package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.enums.PaymentPurpose;
import com.swp391.evdealersystem.entity.Payment;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {
    Payment createVnPayPayment(Long orderId, PaymentPurpose purpose, BigDecimal amount, String clientIp);
    public Payment createCashPayment(Long orderId, PaymentPurpose purpose, BigDecimal amount);
    String handleReturn(Map<String, String> allParams);
    String handleIpn(Map<String, String> allParams);
}
