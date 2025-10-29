package com.swp391.evdealersystem.dto.response;

import lombok.Data;

import java.time.Instant;
@Data
public class ServiceRecordResponse {
    private Long id;
    private Long userId;
    private Long customerId;
    private Long serviceId;
    private String content;
    private String note;
    private Instant createdAt;
    private Instant updatedAt;
}
