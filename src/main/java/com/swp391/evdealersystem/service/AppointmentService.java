package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.UpdateAppointmentStatusRequest;
import com.swp391.evdealersystem.entity.Appointment;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.ServiceEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    // Tạo cuộc hẹn
    Appointment createAppointment(Long customer, Long serviceId, LocalDateTime startAt, LocalDateTime endAt);

    // Kiểm tra số lượng cuộc hẹn đã có trong cùng slot
    boolean isSlotAvailable(Long serviceId, LocalDateTime startAt, LocalDateTime endAt);

    // Kiểm tra xem khách hàng đã có lịch hẹn cho dịch vụ này trong slot này chưa
    boolean hasAppointmentForService(Long customerId, Long serviceId, LocalDateTime startAt, LocalDateTime endAt);

    // CRUD các cuộc hẹn
    List<Appointment> getAppointmentsByCustomer(Long customerId);
    void cancelAppointment(Long appointmentId);

    // Kiểm tra số lượng slot còn trống
    long remainingSlots(Long dealershipId , Long serviceId, LocalDateTime startAt, LocalDateTime endAt);

    // Cập nhật trạng thái của cuộc hẹn
    void updateStatus(Long appointmentId, UpdateAppointmentStatusRequest req);
}
