package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ĐẾM SLOT: chỉ tính SCHEDULED
    @Query(value = """
      SELECT COUNT(*)
      FROM appointments
      WHERE warehouse_id = ?1
        AND service_id   = ?2
        AND start_at     = ?3
        AND end_at       = ?4
        AND status IN ('SCHEDULED')
      """, nativeQuery = true)
    long countBooked(Long warehouseId, Long serviceId,
                     LocalDateTime startAt, LocalDateTime endAt);

    // KH đã có lịch trong cùng slot & địa điểm chưa
    boolean existsByCustomerIdAndWarehouseIdAndStartAtAndEndAt(
            Long customerId, Long warehouseId, LocalDateTime startAt, LocalDateTime endAt);

    // GET danh sách theo khoảng thời gian + lọc tùy chọn
    @Query("""
     SELECT a FROM Appointment a
      WHERE a.startAt < :to AND a.endAt > :from
        AND (:warehouseId IS NULL OR a.warehouseId = :warehouseId)
        AND (:serviceId   IS NULL OR a.serviceId   = :serviceId)
        AND (:customerId  IS NULL OR a.customerId  = :customerId)
      ORDER BY a.startAt ASC
  """)
    List<Appointment> findByFilters(@Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to,
                                    @Param("warehouseId") Long warehouseId,
                                    @Param("serviceId") Long serviceId,
                                    @Param("customerId") Long customerId);
}
