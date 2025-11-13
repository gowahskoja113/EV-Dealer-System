package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.CreateAppointmentRequest;
import com.swp391.evdealersystem.dto.request.UpdateAppointmentStatusRequest;
import com.swp391.evdealersystem.dto.response.AppointmentResponse;
import com.swp391.evdealersystem.entity.Appointment;
import com.swp391.evdealersystem.enums.AppointmentStatus;
import com.swp391.evdealersystem.exception.BadRequestException;
import com.swp391.evdealersystem.exception.ConflictException;
import com.swp391.evdealersystem.exception.NotFoundException;
import com.swp391.evdealersystem.mapper.AppointmentMapper;
import com.swp391.evdealersystem.repository.AppointmentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository repo;
    private final JdbcTemplate jdbc;
    private final AppointmentMapper mapper;

    public AppointmentServiceImpl(AppointmentRepository repo,
                                  JdbcTemplate jdbc,
                                  AppointmentMapper mapper) {
        this.repo = repo;
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    // ===== helper lock tối giản với MySQL GET_LOCK =====
    private String fmt(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    private String lockKey(Long wh, Long srv, String s, String e) {
        return "booking:" + wh + ":" + srv + ":" + s + ":" + e;
    }
    private boolean getLock(String key, Duration timeout) {
        Integer got = jdbc.queryForObject("SELECT GET_LOCK(?, ?)", Integer.class, key, (int) timeout.getSeconds());
        return got != null && got == 1;
    }
    private void releaseLock(String key) {
        jdbc.execute("SELECT RELEASE_LOCK('" + key + "')");
    }

    // ===== nghiệp vụ =====
    @Override
    @Transactional
    public AppointmentResponse create(CreateAppointmentRequest r) {
        if (r.getStartAt() == null || r.getEndAt() == null || !r.getStartAt().isBefore(r.getEndAt())) {
            throw new BadRequestException("startAt phải < endAt");
        }

        String s = fmt(r.getStartAt()), e = fmt(r.getEndAt());
        String key = lockKey(r.getWarehouseId(), r.getServiceId(), s, e);

        if (!getLock(key, Duration.ofSeconds(5))) {
            throw new ConflictException("Khung giờ đang bận, thử lại sau");
        }

        try {
            // 1) chặn trùng lịch của cùng customer trong chính slot đó
            // SỬA TẠI ĐÂY: Dùng tên phương thức mới trong Repository
            boolean dup = repo.existsByCustomerCustomerIdAndWarehouseIdAndStartAtAndEndAt(
                    r.getCustomerId(), r.getWarehouseId(), r.getStartAt(), r.getEndAt());
            if (dup) throw new ConflictException("Khách đã có lịch trong khung giờ này");

            // 2) capacity 10 chỗ (tính theo SCHEDULED
            long booked = repo.countBooked(
                    r.getWarehouseId(), r.getServiceId(), r.getStartAt(), r.getEndAt()
            );

            // TODO: Bổ sung logic kiểm tra nếu (booked >= 10) thì throw ConflictException

            // 3) map DTO -> entity (dùng mapper), save → map entity -> response
            Appointment a = mapper.toEntity(r);
            a = repo.save(a);
            return mapper.toResponse(a);

        } catch (DataIntegrityViolationException ex) {
            // backup nếu UNIQUE uq_customer_slot nổ do race
            throw new ConflictException("Khách đã có lịch trong khung giờ này");
        } finally {
            releaseLock(key);
        }
    }

    @Override
    @Transactional
    public void cancel(Long appointmentId, Clock clock) {
        Appointment a = repo.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy lịch"));

        if (a.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new ConflictException("Chỉ hủy được lịch ở trạng thái SCHEDULED");
        }

        if (!LocalDateTime.now(clock).isBefore(a.getStartAt())) {
            throw new ConflictException("Chỉ hủy trước giờ hẹn");
        }

        a.setStatus(AppointmentStatus.CANCELED);
        repo.save(a); // slot tự trả vì COUNT chỉ tính SCHEDULED
    }

    @Override
    @Transactional(readOnly = true)
    public long remainingSlots(Long warehouseId, Long serviceId, LocalDateTime startAt, LocalDateTime endAt) {
        long booked = repo.countBooked(warehouseId, serviceId, startAt, endAt);
        return Math.max(0, 10 - booked);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void updateStatus(Long id, UpdateAppointmentStatusRequest req, java.time.Clock clock) {
        var a = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy lịch"));

        if (req.getStatus() == null)
            throw new BadRequestException("status không được null");

        var now = java.time.LocalDateTime.now(clock);
        switch (req.getStatus()) {
            case CANCELED -> {

                 if (!now.isBefore(a.getStartAt())) throw new ConflictException("Chỉ hủy trước giờ hẹn");
                a.setStatus(AppointmentStatus.CANCELED);
            }
            case IN_SERVICE -> {
                if (a.getStatus() != AppointmentStatus.SCHEDULED)
                    throw new ConflictException("Chỉ chuyển IN_SERVICE từ SCHEDULED");
                if (now.isBefore(a.getStartAt()))
                    throw new ConflictException("Chưa đến giờ bắt đầu");
                a.setStatus(AppointmentStatus.IN_SERVICE);
            }
            case COMPLETED -> {
                if (a.getStatus() != AppointmentStatus.IN_SERVICE)
                    throw new ConflictException("Chỉ hoàn tất từ IN_SERVICE");
                a.setStatus(AppointmentStatus.COMPLETED);
            }
            case SCHEDULED -> {
                throw new ConflictException("Không chuyển ngược về SCHEDULED");
            }
        }
        if (req.getNote() != null) a.setNote(req.getNote());
        repo.save(a);
    }

}
