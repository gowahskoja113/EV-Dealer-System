package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponse {

    private Long appointmentId;
    private String customerName;
    private String customerPhone;
    private String serviceName;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private AppointmentStatus status;
}
