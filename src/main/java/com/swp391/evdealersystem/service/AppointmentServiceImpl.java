package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.AppointmentRequest;
import com.swp391.evdealersystem.dto.request.UpdateAppointmentStatusRequest;
import com.swp391.evdealersystem.entity.Appointment;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.ServiceEntity;
import com.swp391.evdealersystem.entity.Slot;
import com.swp391.evdealersystem.enums.ServiceType;
import com.swp391.evdealersystem.mapper.AppointmentMapper;
import com.swp391.evdealersystem.repository.AppointmentRepository;
import com.swp391.evdealersystem.repository.SlotRepository;
import com.swp391.evdealersystem.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final AppointmentMapper appointmentMapper;

    private final SlotRepository slotRepository;


    @Override
    public Appointment createAppointment(Long customer, Long serviceId, LocalDateTime startAt, LocalDateTime endAt) {
        Customer customers = new Customer();

        // Kiểm tra số lượng slot đã đạt giới hạn cho dịch vụ
        if (!isSlotAvailable(serviceId, startAt, endAt)) {
            throw new RuntimeException("Số lượng slot đã đầy trong khoảng thời gian này");
        }

        // Kiểm tra xem khách hàng đã có lịch hẹn cho dịch vụ này trong slot này chưa
        if (hasAppointmentForService(customer, serviceId, startAt, endAt)) {
            throw new RuntimeException("Khách hàng đã có lịch hẹn cho dịch vụ này trong thời gian này");
        }

        // Tạo cuộc hẹn từ CreateAppointmentRequest và chuyển thành Appointment entity
        Appointment appointment = appointmentMapper.toEntity(new AppointmentRequest());

        // Lưu cuộc hẹn vào cơ sở dữ liệu
        return appointmentRepository.save(appointment);
    }

    @Override
    public boolean isSlotAvailable(Long serviceId, LocalDateTime startAt, LocalDateTime endAt) {
        // Kiểm tra số lượng cuộc hẹn đã có cho serviceId, startAt và endAt
        return appointmentRepository.countAppointmentsInSlot(serviceId, startAt, endAt) < 10; // Giới hạn 10 cuộc hẹn
    }

    @Override
    public boolean hasAppointmentForService(Long customerId, Long serviceId, LocalDateTime startAt, LocalDateTime endAt) {
        // Kiểm tra xem khách hàng đã có lịch hẹn cho dịch vụ này trong slot này chưa
        return appointmentRepository.existsByCustomerCustomerIdAndServiceIdAndStartAtAndEndAt(customerId, serviceId, startAt, endAt);
    }

    @Override
    public List<Appointment> getAppointmentsByCustomer(Long customerId) {
        return appointmentRepository.findByCustomerCustomerId(customerId);
    }

    @Override
    public void cancelAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Lấy Slot từ Appointment
        Slot slot = appointment.getSlot();  // Lấy Slot từ Appointment

        // Kiểm tra và giảm số lượng đăng ký cho dịch vụ trong Slot
        if (appointment.getService() != null) {
            if (appointment.getService().getServiceType() == ServiceType.TEST_DRIVE) {
                slot.decrementTestDriveCount();  // Giảm số lượng lái thử
            } else if (appointment.getService().getServiceType() == ServiceType.SERVICE) {
                slot.decrementServiceCount();  // Giảm số lượng bảo dưỡng
            }
        }

        // Hủy cuộc hẹn
        appointmentRepository.delete(appointment);
        slotRepository.save(slot);  // Cập nhật lại số lượng slot trong cơ sở dữ liệu
    }

    @Override
    public long remainingSlots(Long dealershipId, Long serviceId, LocalDateTime startAt, LocalDateTime endAt) {
        return 0;
    }

    @Override
    public void updateStatus(Long appointmentId, UpdateAppointmentStatusRequest req) {
        // Tìm cuộc hẹn theo ID
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Cập nhật trạng thái cuộc hẹn và ghi chú
        appointment.setStatus(req.getStatus());
        appointment.setNote(req.getNote());

        // Lưu lại cuộc hẹn đã được cập nhật
        appointmentRepository.save(appointment);
    }

//    @Override
//    public long remainingSlots(Long serviceId, LocalDateTime startAt, LocalDateTime endAt) {
//        return 10 - appointmentRepository.countAppointmentsInSlot(serviceId, startAt, endAt); // Giới hạn 10 slot
//    }
}
