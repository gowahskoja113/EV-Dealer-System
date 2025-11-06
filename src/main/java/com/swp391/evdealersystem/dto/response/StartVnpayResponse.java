package com.swp391.evdealersystem.dto.response;

public record StartVnpayResponse(
        Long paymentId,
        String paymentUrl
) {}
