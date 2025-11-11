package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    Optional<ServiceEntity> findByNameIgnoreCase(String name);
}
