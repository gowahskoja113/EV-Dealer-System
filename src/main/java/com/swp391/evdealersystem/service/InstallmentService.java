package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.GeneratePlanRequest;
import com.swp391.evdealersystem.dto.response.InstallmentResponse;

import java.util.List;

public interface InstallmentService {
    List<InstallmentResponse> generatePlan(Long orderId, GeneratePlanRequest req);
    List<InstallmentResponse> listByOrder(Long orderId);
}
