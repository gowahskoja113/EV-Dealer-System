package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {

    // Tìm Slot theo thời gian bắt đầu và kết thúc
    List<Slot> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    // Tìm Slot theo thời gian bắt đầu
    List<Slot> findByStartTime(LocalDateTime startTime);
}
