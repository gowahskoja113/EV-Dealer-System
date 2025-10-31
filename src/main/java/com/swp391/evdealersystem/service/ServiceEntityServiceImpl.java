package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ServiceRequest;
import com.swp391.evdealersystem.dto.response.ServiceResponse;
import com.swp391.evdealersystem.entity.ServiceEntity;
import com.swp391.evdealersystem.repository.ServiceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceEntityServiceImpl implements ServiceEntityService{

    private final ServiceRepository repo;

    @Override
    public ServiceResponse create(ServiceRequest req) {
        repo.findByNameIgnoreCase(req.getName())
                .ifPresent(x -> { throw new IllegalArgumentException("Tên dịch vụ đã tồn tại"); });

        ServiceEntity e = new ServiceEntity();
        e.setName(req.getName());
        e.setDescription(req.getDescription());

        e = repo.save(e);
        return toResponse(e);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse get(Long id) {
        ServiceEntity e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServiceItem not found: " + id));
        return toResponse(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceResponse> list(Pageable p) {
        return repo.findAll(p).map(this::toResponse);
    }

    @Override
    public ServiceResponse update(Long id, ServiceRequest req) {
        ServiceEntity e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServiceItem not found: " + id));

        if (req.getName() != null) e.setName(req.getName());
        if (req.getDescription() != null) e.setDescription(req.getDescription());

        e= repo.save(e);
        return toResponse(e);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private ServiceResponse toResponse(ServiceEntity entity) {
        ServiceResponse response = new ServiceResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        return response;
    }
}

