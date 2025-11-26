package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.ServiceRequest;
import com.swp391.evdealersystem.dto.response.ServiceResponse;
import com.swp391.evdealersystem.entity.ServiceEntity;
import com.swp391.evdealersystem.mapper.ServiceEntityMapper;
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
public class ServiceEntityServiceImpl implements ServiceEntityService {

    private final ServiceRepository repo;
    private final ServiceEntityMapper mapper;

    @Override
    public ServiceResponse create(ServiceRequest req) {
        // map DTO -> Entity (đã có name, description, serviceType)
        ServiceEntity entity = mapper.toEntity(req);
        ServiceEntity saved = repo.save(entity);
        // map Entity -> Response
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse get(Long id) {
        ServiceEntity e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServiceItem not found: " + id));
        return mapper.toResponse(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceResponse> list(Pageable p) {
        return repo.findAll(p)
                .map(mapper::toResponse);
    }

    @Override
    public ServiceResponse update(Long id, ServiceRequest req) {
        ServiceEntity e = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ServiceItem not found: " + id));

        // update từ DTO -> Entity (chỉ set field nào không null)
        mapper.updateEntity(e, req);

        ServiceEntity saved = repo.save(e);
        return mapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
