package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Order;
import com.swp391.evdealersystem.enums.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Override
    @EntityGraph(attributePaths = {"customer", "serial", "serial.vehicle", "serial.vehicle.model"})
    List<Order> findAll();

    @EntityGraph(attributePaths = {"customer", "serial", "serial.vehicle", "serial.vehicle.model"})
    List<Order> findByCustomer_CustomerId(Long customerId);

    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.customer c " +
            "LEFT JOIN FETCH o.serial s " +
            "LEFT JOIN FETCH s.vehicle v " +
            "LEFT JOIN FETCH v.model m " +
            "WHERE v.vehicleId = :vehicleId")
    List<Order> findOrdersByVehicleIdWithGraph(@Param("vehicleId") Long vehicleId);

    @EntityGraph(attributePaths = {"customer", "serial", "serial.vehicle", "serial.vehicle.model"})
    Optional<Order> findGraphByOrderId(Long orderId);

    boolean existsBySerial_VinAndStatus(String vin, OrderStatus status);

    @EntityGraph(attributePaths = {"customer", "serial", "serial.vehicle", "serial.vehicle.model"})
    @Query("""
        SELECT o FROM Order o
        LEFT JOIN FETCH o.customer c
        LEFT JOIN FETCH o.serial s
        LEFT JOIN FETCH s.vehicle v
        LEFT JOIN FETCH v.model m
        WHERE c.customerId = :customerId
          AND o.depositAmount > 0
          AND (o.status <> com.swp391.evdealersystem.enums.OrderStatus.CANCELED)
          AND (:orderId IS NULL OR o.orderId = :orderId)
        ORDER BY o.orderDate DESC, o.orderId DESC
    """)
    List<Order> findDepositedOrdersByCustomer(
            @Param("customerId") Long customerId,
            @Param("orderId") Long orderId
    );

    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.customer c " +
            "JOIN FETCH o.serial s " +
            "JOIN FETCH s.vehicle v " +
            "JOIN FETCH v.model m " +
            "WHERE o.orderId = :orderId")
    Optional<Order> findOrderDetailsForContract(@Param("orderId") Long orderId);
}
