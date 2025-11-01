package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CreateAppointmentRequest {
    @NotNull private Long customerId;
    @NotNull private Long warehouseId;
    @NotNull private Long serviceId;
    @NotNull private LocalDateTime startAt;
    @NotNull private LocalDateTime endAt;
    @NotNull private Long assignedUserId;
    private String note;

}
