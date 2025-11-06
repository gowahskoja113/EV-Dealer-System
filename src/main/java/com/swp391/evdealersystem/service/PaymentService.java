package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.CashPaymentRequest;
import com.swp391.evdealersystem.dto.request.StartVnpayRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;
import com.swp391.evdealersystem.dto.response.StartVnpayResponse;
import com.swp391.evdealersystem.dto.response.VnpIpnResponse;
import com.swp391.evdealersystem.enums.PaymentPurpose;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {
    public OrderResponse processCash(Long orderId, CashPaymentRequest req);

    StartVnpayResponse startVnpay(Long orderId, StartVnpayRequest req);

    VnpIpnResponse processVnpayCallback(Map<String, String> params);
}
