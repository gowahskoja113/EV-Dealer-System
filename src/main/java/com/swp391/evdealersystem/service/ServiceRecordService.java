package com.swp391.evdealersystem.service;


import com.swp391.evdealersystem.dto.request.ServiceRecordRequest;
import com.swp391.evdealersystem.dto.request.ServiceRecordUpdateRequest;
import com.swp391.evdealersystem.dto.response.ServiceRecordResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceRecordService {
    ServiceRecordResponse create(ServiceRecordRequest req);
    ServiceRecordResponse get(Long id);
    Page<ServiceRecordResponse> list(Pageable p);
    Page<ServiceRecordResponse> byCustomer(Long customerId, Pageable p);
    Page<ServiceRecordResponse> byServiceItem(Long itemId, Pageable p);
    Page<ServiceRecordResponse> byUser(Long userId, Pageable p);
    ServiceRecordResponse update(Long id, ServiceRecordUpdateRequest req);
    void delete(Long id);
}
