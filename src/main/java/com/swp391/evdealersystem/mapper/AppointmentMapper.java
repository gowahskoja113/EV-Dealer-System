
package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.CreateAppointmentRequest;
import com.swp391.evdealersystem.dto.response.AppointmentResponse;
import com.swp391.evdealersystem.entity.Appointment;
import com.swp391.evdealersystem.enums.AppointmentStatus;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toEntity(CreateAppointmentRequest r) {
        Appointment a = new Appointment();
        a.setCustomerId(r.getCustomerId());
        a.setWarehouseId(r.getWarehouseId());
        a.setServiceId(r.getServiceId());
        a.setAssignedUserId(r.getAssignedUserId());
        a.setStartAt(r.getStartAt());
        a.setEndAt(r.getEndAt());
        a.setStatus(AppointmentStatus.SCHEDULED);
        a.setNote(r.getNote());
        return a;
    }

    public AppointmentResponse toResponse(Appointment a) {
        return new AppointmentResponse(a.getAppointmentId(), a.getStatus());
    }
}
