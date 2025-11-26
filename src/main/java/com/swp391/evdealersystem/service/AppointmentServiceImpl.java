package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.AppointmentRequest;
import com.swp391.evdealersystem.dto.request.UpdateAppointmentStatusRequest;
import com.swp391.evdealersystem.dto.response.AppointmentResponse;
import com.swp391.evdealersystem.entity.Appointment;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.ServiceEntity;
import com.swp391.evdealersystem.entity.Slot;
import com.swp391.evdealersystem.enums.ServiceType;
import com.swp391.evdealersystem.mapper.AppointmentMapper;
import com.swp391.evdealersystem.repository.AppointmentRepository;
import com.swp391.evdealersystem.repository.ServiceRepository;
import com.swp391.evdealersystem.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final AppointmentMapper appointmentMapper;

    private final SlotRepository slotRepository;

    private final ServiceRepository serviceRepository;

    @Override
    public Appointment createAppointment(AppointmentRequest req) {

        // 1. Lấy Slot
        Slot slot = slotRepository.findById(req.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        // 2. Lấy Service riêng, khỏi phụ thuộc mapper
        ServiceEntity service = serviceRepository.findById(req.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // 3. Dùng thời gian của Slot
        LocalDateTime startAt = slot.getStartTime();
        LocalDateTime endAt   = slot.getEndTime();

        // 4. Check slot full theo loại dịch vụ
        if (!isSlotAvailable(slot.getSlotId(), service.getServiceType())) {
            // Đổi 500 thành 400 cho dễ debug
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Slot is full for this service type"
            );
        }

        // 5. Check khách trùng lịch (customer + service + slot)
        if (hasAppointmentForService(req.getCustomerId(), req.getServiceId(), req.getSlotId())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT,
                    "Customer already has an appointment in this slot"
            );
        }

        // 6. Map request -> Appointment
        Appointment appointment = appointmentMapper.toEntity(req);
        appointment.setSlot(slot);
        appointment.setStartAt(startAt);
        appointment.setEndAt(endAt);

        // 7. Lưu appointment
        Appointment saved = appointmentRepository.save(appointment);

        // 8. Tăng count trong slot
        if (service.getServiceType() == ServiceType.TEST_DRIVE) {
            slot.incrementTestDriveCount();
        } else if (service.getServiceType() == ServiceType.MAINTENANCE) {
            slot.incrementServiceCount();
        }
        slotRepository.save(slot);

        return saved;
    }


    @Override
    public boolean isSlotAvailable(Long slotId, ServiceType serviceType) {
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        if (serviceType == ServiceType.TEST_DRIVE) {
            return slot.getTestDriveCount() < slot.getMaxTestDrive();
        }

        if (serviceType == ServiceType.MAINTENANCE) {
            return slot.getServiceCount() < slot.getMaxService();
        }

        return false;
    }


    @Override
    public boolean hasAppointmentForService(Long customerId, Long serviceId, Long slotId) {
        // Kiểm tra xem khách hàng đã có lịch hẹn cho dịch vụ này trong slot này chưa
        return appointmentRepository
                .existsByCustomerCustomerIdAndService_IdAndSlot_SlotId(customerId, serviceId, slotId);
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
            } else if (appointment.getService().getServiceType() == ServiceType.MAINTENANCE) {
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

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponse)
                .toList();
    }

//    @Override
//    public long remainingSlots(Long serviceId, LocalDateTime startAt, LocalDateTime endAt) {
//        return 10 - appointmentRepository.countAppointmentsInSlot(serviceId, startAt, endAt); // Giới hạn 10 slot
//    }
}
