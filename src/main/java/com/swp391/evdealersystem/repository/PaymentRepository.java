package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Payment;
import com.swp391.evdealersystem.enums.PaymentPurpose;
import com.swp391.evdealersystem.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByVnpTxnRef(String vnpTxnRef);
    Optional<Payment> findTopByOrder_OrderIdAndPurposeAndStatusOrderByPaidAtDesc(
            Long orderId, PaymentPurpose purpose, PaymentStatus status);
    List<Payment> findByOrder_OrderIdOrderByCreatedAtDesc(Long orderId);

}
