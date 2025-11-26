package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.AppointmentRequest;
import com.swp391.evdealersystem.dto.request.UpdateAppointmentStatusRequest;
import com.swp391.evdealersystem.dto.response.AppointmentResponse;
import com.swp391.evdealersystem.entity.Appointment;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.ServiceEntity;
import com.swp391.evdealersystem.enums.ServiceType;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    // Tạo cuộc hẹn
    Appointment createAppointment(AppointmentRequest req);


    // Kiểm tra số lượng cuộc hẹn đã có trong cùng slot
    boolean isSlotAvailable(Long slotId, ServiceType serviceType);


    // check coi ng dum co cuoc hen do chua
    boolean hasAppointmentForService(Long customerId, Long serviceId, Long slotId);


    // CRUD các cuộc hẹn
    List<Appointment> getAppointmentsByCustomer(Long customerId);
    void cancelAppointment(Long appointmentId);

    // Kiểm tra số lượng slot còn trống
    long remainingSlots(Long dealershipId , Long serviceId, LocalDateTime startAt, LocalDateTime endAt);

    // Cập nhật trạng thái của cuộc hẹn
    void updateStatus(Long appointmentId, UpdateAppointmentStatusRequest req);

    //get all appointments
    List<AppointmentResponse> getAllAppointments();
}
