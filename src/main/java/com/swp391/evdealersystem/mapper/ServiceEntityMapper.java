package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.ServiceRequest;
import com.swp391.evdealersystem.dto.response.ServiceResponse;
import com.swp391.evdealersystem.entity.ServiceEntity;
import org.springframework.stereotype.Component;

@Component
public class ServiceEntityMapper {

    public ServiceEntity toEntity(ServiceRequest req) {
        ServiceEntity e = new ServiceEntity();
        e.setName(req.getName());
        e.setDescription(req.getDescription());
        e.setServiceType(req.getServiceType());
        return e;
    }

    public void updateEntity(ServiceEntity e, ServiceRequest req) {
        if (req.getName() != null) {
            e.setName(req.getName());
        }
        if (req.getDescription() != null) {
            e.setDescription(req.getDescription());
        }
        if (req.getServiceType() != null) {
            e.setServiceType(req.getServiceType());
        }
    }

    public ServiceResponse toResponse(ServiceEntity entity) {
        ServiceResponse resp = new ServiceResponse();
        resp.setId(entity.getId());
        resp.setName(entity.getName());
        resp.setDescription(entity.getDescription());
        resp.setServiceType(entity.getServiceType());
        return resp;
    }
}
