package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ServiceRecordRequest;
import com.swp391.evdealersystem.dto.request.ServiceRecordUpdateRequest;
import com.swp391.evdealersystem.dto.response.ServiceRecordResponse;
import com.swp391.evdealersystem.entity.ServiceRecord;
import com.swp391.evdealersystem.mapper.ServiceRecordMapper;
import com.swp391.evdealersystem.repository.CustomerRepository;
import com.swp391.evdealersystem.repository.ServiceRecordRepository;
import com.swp391.evdealersystem.repository.ServiceRepository;
import com.swp391.evdealersystem.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceRecordServiceImpl implements ServiceRecordService {

    private final ServiceRecordRepository repo;
    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;
    private final ServiceRepository serviceRepo;
    private final ServiceRecordMapper mapper;

    @Override
    public ServiceRecordResponse create(ServiceRecordRequest req){
        ServiceRecord e = new ServiceRecord();
        e.setUser(userRepo.findById(req.getUserId()).orElseThrow(() -> nf("User", req.getUserId())));
        e.setCustomer(customerRepo.findById(req.getCustomerId()).orElseThrow(() -> nf("Customer", req.getCustomerId())));
        e.setService(serviceRepo.findById(req.getServiceId()).orElseThrow(() -> nf("ServiceEntity", req.getServiceId())));
        e.setContent(req.getContent());
        e.setNote(req.getNote());
        return mapper.toResponse(repo.save(e));
    }

    @Override @Transactional(readOnly = true)
    public ServiceRecordResponse get(Long id){
        return mapper.toResponse(repo.findById(id).orElseThrow(() -> nf("ServiceRecord", id)));
    }

    @Override @Transactional(readOnly = true)
    public Page<ServiceRecordResponse> list(Pageable p){
        return repo.findAll(p).map(mapper::toResponse);
    }

    @Override @Transactional(readOnly = true)
    public Page<ServiceRecordResponse> byCustomer(Long customerId, Pageable p){
        return repo.findByCustomer_CustomerId(customerId, p).map(mapper::toResponse);
    }

    @Override @Transactional(readOnly = true)
    public Page<ServiceRecordResponse> byServiceEntity(Long serviceId, Pageable p){
        return repo.findByService_Id(serviceId, p).map(mapper::toResponse);
    }

    @Override @Transactional(readOnly = true)
    public Page<ServiceRecordResponse> byUser(Long userId, Pageable p){
        return repo.findByUser_UserId(userId, p).map(mapper::toResponse);
    }



    @Override
    public ServiceRecordResponse update(Long id, ServiceRecordUpdateRequest req){
        ServiceRecord e = repo.findById(id).orElseThrow(() -> nf("ServiceRecord", id));
        if(req.getContent()!=null) e.setContent(req.getContent());
        if(req.getNote()!=null) e.setNote(req.getNote());
        return mapper.toResponse(repo.save(e));
    }

    @Override
    public void delete(Long id){ repo.deleteById(id); }

    private EntityNotFoundException nf(String type, Object id){
        return new EntityNotFoundException(type+" not found: "+id);
    }
}

