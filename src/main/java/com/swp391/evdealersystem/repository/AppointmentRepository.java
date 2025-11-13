package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Kiểm tra số lượng cuộc hẹn đã đạt giới hạn cho một slot (dịch vụ cụ thể)
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.service.id = :serviceId AND a.startAt = :startAt AND a.endAt = :endAt")
    long countAppointmentsInSlot(Long serviceId, LocalDateTime startAt, LocalDateTime endAt);

    // Kiểm tra xem khách hàng đã có cuộc hẹn nào trong cùng slot và dịch vụ (lái thử)
    boolean existsByCustomerCustomerIdAndServiceIdAndStartAtAndEndAt(
            Long customerId, Long serviceId, LocalDateTime startAt, LocalDateTime endAt);

    List<Appointment> findByCustomerCustomerId(Long customerId);

}
