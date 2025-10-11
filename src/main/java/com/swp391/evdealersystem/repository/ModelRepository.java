package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModelRepository extends JpaRepository<Model, Long> {
    Optional<Model> findByModelCode(String modelCode);
    boolean existsByModelCode(String modelCode);
}