package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    Optional<Service> findByNameIgnoreCase(String name);
}