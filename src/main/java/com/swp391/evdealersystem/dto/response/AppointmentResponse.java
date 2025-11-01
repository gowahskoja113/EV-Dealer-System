package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppointmentResponse {
    private Long appointmentId;
    private AppointmentStatus status;
}

