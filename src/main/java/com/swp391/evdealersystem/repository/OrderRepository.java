package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Override
    @EntityGraph(attributePaths = {"customer", "vehicle", "vehicle.model", "installments"})
    List<Order> findAll();

    @EntityGraph(attributePaths = {"customer", "vehicle", "vehicle.model", "installments"})
    List<Order> findByCustomer_CustomerId(Long customerId);

    @EntityGraph(attributePaths = {"customer", "vehicle", "vehicle.model", "installments"})
    List<Order> findByVehicle_VehicleId(Long vehicleId);

    @EntityGraph(attributePaths = {"customer", "vehicle", "vehicle.model", "installments"})
    Optional<Order> findGraphByOrderId(Long orderId);

}
