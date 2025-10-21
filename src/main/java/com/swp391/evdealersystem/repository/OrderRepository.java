package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // fetch customer & vehicle & model để tránh N+1 khi toResponse
    @Override
    @EntityGraph(attributePaths = {"customer", "vehicle", "vehicle.model"})
    List<Order> findAll();

    @EntityGraph(attributePaths = {"customer", "vehicle", "vehicle.model"})
    List<Order> findByCustomer_CustomerId(Long customerId);

    @EntityGraph(attributePaths = {"customer", "vehicle", "vehicle.model"})
    List<Order> findByVehicle_VehicleId(Long vehicleId);
}