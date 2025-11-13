package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.AppointmentRequest;
import com.swp391.evdealersystem.dto.request.UpdateAppointmentStatusRequest;
import com.swp391.evdealersystem.dto.response.AppointmentResponse;
import com.swp391.evdealersystem.entity.Appointment;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.repository.AppointmentRepository;
import com.swp391.evdealersystem.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;
    private final AppointmentRepository repo;


    @PostMapping
    public AppointmentResponse create(@Valid @RequestBody AppointmentRequest req) {
        // Gọi service để tạo cuộc hẹn và trả về AppointmentResponse
        Appointment appointment = service.createAppointment(req.customerId,req.serviceId,req.startAt,req.endAt);
        return new AppointmentResponse(appointment.getAppointmentId(), appointment.getStatus());
    }

    /**
     * Hủy lịch trước giờ hẹn → slot tự trả lại (COUNT không tính CANCELED)
     */
    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id) {
        service.cancelAppointment(id);
    }

    /**
     * Số chỗ còn trống của 1 slot (capacity 10)
     */
    @GetMapping("/remaining")
    public long remaining(
            @RequestParam Long dealershipId,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt) {
        return service.remainingSlots(dealershipId, serviceId, startAt, endAt);
    }

    @GetMapping("/{id}")
    public Appointment getById(@PathVariable Long id) {
        return repo.findById(id).orElse(null); // cần NotFound thì throw tùy bạn
    }


    /**
     * Cập nhật trạng thái cuộc hẹn (SCHEDULED, IN_SERVICE, COMPLETED, CANCELED)
     */
    @PatchMapping("/{id}/status")
    public void updateStatus(
             Long id,
            @RequestBody UpdateAppointmentStatusRequest req) {

        service.updateStatus(id, req);
    }
}
