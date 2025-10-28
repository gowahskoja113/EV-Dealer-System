package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ManualPayRequest;

public interface PaymentService {
    void manualPay(Long orderId, ManualPayRequest req);
}
