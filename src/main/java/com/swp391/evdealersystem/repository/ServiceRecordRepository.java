package com.swp391.evdealersystem.repository;


import com.swp391.evdealersystem.entity.ServiceRecord;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;

public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {
    @EntityGraph(attributePaths={"user","customer","serviceItem"})
    Page<ServiceRecord> findByCustomer_Id(Long customerId, Pageable p);

    @EntityGraph(attributePaths={"user","customer","serviceItem"})
    Page<ServiceRecord> findByServiceItem_Id(Long serviceItemId, Pageable p);

    @EntityGraph(attributePaths={"user","customer","serviceItem"})
    Page<ServiceRecord> findByUser_Id(Long userId, Pageable p);
}

