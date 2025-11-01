package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.CreateAppointmentRequest;
import com.swp391.evdealersystem.dto.response.AppointmentResponse;
import com.swp391.evdealersystem.entity.Appointment;
import com.swp391.evdealersystem.repository.AppointmentRepository;
import com.swp391.evdealersystem.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    private final AppointmentRepository repo;



    /**
     * Tạo lịch (1 dịch vụ/khung giờ), kiểm tra:
     * - Không trùng lịch cho cùng customer trong cùng slot
     * - Capacity tối đa 10 (theo warehouse + service + start/end)
     * - Lock slot bằng MySQL GET_LOCK để tránh race
     */
    @PostMapping
    public AppointmentResponse create(@Valid @RequestBody CreateAppointmentRequest req) {
        return service.create(req);
    }

    /**
     * Hủy lịch trước giờ hẹn → slot tự trả lại (COUNT không tính CANCELED)
     */
    @PostMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id) {
        service.cancel(id, java.time.Clock.systemDefaultZone());
    }

    /**
     * Số chỗ còn trống của 1 slot (capacity 10)
     */
    @GetMapping("/remaining")
    public long remaining(
            @RequestParam Long warehouseId,
            @RequestParam Long serviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startAt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endAt) {

        return service.remainingSlots(warehouseId, serviceId, startAt, endAt);
    }

    @GetMapping("/{id}")
    public Appointment getById(@PathVariable Long id) {
        return repo.findById(id).orElse(null); // cần NotFound thì throw tùy bạn
    }

    // GET danh sách theo khoảng thời gian + lọc tùy chọn (warehouseId, serviceId, customerId)
    @GetMapping
    public List<Appointment> list(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) Long customerId) {

        return repo.findByFilters(from, to, warehouseId, serviceId, customerId);
    }

    @PatchMapping("/{id}/status")
    public org.springframework.http.ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestBody com.swp391.evdealersystem.dto.request.UpdateAppointmentStatusRequest req) {

        service.updateStatus(id, req, java.time.Clock.systemDefaultZone());
        return org.springframework.http.ResponseEntity.noContent().build(); // 204
    }
}
