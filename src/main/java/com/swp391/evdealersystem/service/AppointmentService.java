package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.CreateAppointmentRequest;
import com.swp391.evdealersystem.dto.response.AppointmentResponse;

import java.time.Clock;
import java.time.LocalDateTime;

public interface AppointmentService {
    AppointmentResponse create(CreateAppointmentRequest req);
    void cancel(Long appointmentId, Clock clock);
    long remainingSlots(Long warehouseId, Long serviceId, LocalDateTime startAt, LocalDateTime endAt);
    void updateStatus(Long id,
                      com.swp391.evdealersystem.dto.request.UpdateAppointmentStatusRequest req,
                      java.time.Clock clock);
}

