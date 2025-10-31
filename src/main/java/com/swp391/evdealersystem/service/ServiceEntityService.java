package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ServiceRequest;
import com.swp391.evdealersystem.dto.response.ServiceResponse;
import com.swp391.evdealersystem.entity.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceEntityService {
    ServiceResponse create(ServiceRequest req);
    ServiceResponse get(Long id);
    Page<ServiceResponse> list(Pageable p);
    ServiceResponse update(Long id, ServiceRequest req);
    void delete(Long id);
}

