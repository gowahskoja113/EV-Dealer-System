package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.response.DepositOrderView;

import java.util.List;

public interface OrderQueryService {
    public List<DepositOrderView> getDepositedOrders(Long customerId, Long orderId);
}

