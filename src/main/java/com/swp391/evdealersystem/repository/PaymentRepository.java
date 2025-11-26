package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Payment;
import com.swp391.evdealersystem.enums.PaymentPurpose;
import com.swp391.evdealersystem.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByOrder_OrderId(Long orderId);;

    Optional<Payment> findByTransactionRef(String transactionRef);

    @Query(value = "SELECT p FROM Payment p " +
            "JOIN p.order o " +
            "JOIN o.serial s " +
            "JOIN s.warehouse w " +
            "JOIN w.dealership d " +
            "LEFT JOIN o.customer c " +
            "WHERE (:dealershipId IS NULL OR d.dealershipId = :dealershipId) " +
            "AND (:customerId IS NULL OR c.id = :customerId) " +
            "AND (:fromDate IS NULL OR p.paymentDate >= :fromDate) " +
            "AND (:toDate IS NULL OR p.paymentDate <= :toDate)",

            countQuery = "SELECT count(p) FROM Payment p " +
                    "JOIN p.order o " +
                    "JOIN o.serial s " +
                    "JOIN s.warehouse w " +
                    "JOIN w.dealership d " +
                    "LEFT JOIN o.customer c " +
                    "WHERE (:dealershipId IS NULL OR d.dealershipId = :dealershipId) " +
                    "AND (:customerId IS NULL OR c.id = :customerId) " +
                    "AND (:fromDate IS NULL OR p.paymentDate >= :fromDate) " +
                    "AND (:toDate IS NULL OR p.paymentDate <= :toDate)")
    Page<Payment> searchPayments(
            @Param("dealershipId") Long dealershipId,
            @Param("customerId") Long customerId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );
}
