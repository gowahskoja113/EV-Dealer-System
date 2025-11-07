package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.Dealership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealershipRepository extends JpaRepository<Dealership, Long> {
}