package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Installment;
import com.swp391.evdealersystem.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface InstallmentRepository extends JpaRepository<Installment, Long> {
    List<Installment> findByOrderOrderIdOrderBySequenceAsc(Long orderId);
    Optional<Installment> findByOrderAndSequence(Order order, Integer sequence);
}