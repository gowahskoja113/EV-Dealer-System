package com.swp391.evdealersystem.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class  AppointmentRequest {

    @NotNull
    private Long customerId;

    @NotNull
    private Long serviceId;

    @NotNull
    private Long assignedUserId;

    @NotNull
    private Long slotId;

    private String note;
}


