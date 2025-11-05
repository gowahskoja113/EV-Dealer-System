package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.CashPaymentRequest;
import com.swp391.evdealersystem.dto.response.OrderResponse;

public interface CashPaymentService {
    public OrderResponse processCash(Long orderId, CashPaymentRequest req);
}
