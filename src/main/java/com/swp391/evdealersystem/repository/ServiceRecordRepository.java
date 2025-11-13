package com.swp391.evdealersystem.repository;

import com.swp391.evdealersystem.entity.ServiceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {

    @EntityGraph(attributePaths = {"user", "customer", "service"})
    Page<ServiceRecord> findByCustomer_CustomerId(Long customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "customer", "service"})
    Page<ServiceRecord> findByUser_UserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "customer", "service"})
    Page<ServiceRecord> findByService_Id(Long serviceId, Pageable pageable);


}
