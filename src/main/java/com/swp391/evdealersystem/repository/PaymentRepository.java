package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Payment;
import com.swp391.evdealersystem.enums.PaymentPurpose;
import com.swp391.evdealersystem.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrder_OrderId(Long orderId);;
    Optional<Payment> findByTransactionRef(String txnRef);
}
