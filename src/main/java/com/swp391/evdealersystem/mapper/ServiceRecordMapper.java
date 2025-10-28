package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.response.ServiceRecordResponse;
import com.swp391.evdealersystem.entity.ServiceRecord;
import org.springframework.stereotype.Component;


@Component
public class ServiceRecordMapper {
    public ServiceRecordResponse toResponse(ServiceRecord e){
        ServiceRecordResponse r = new ServiceRecordResponse();
        r.setId(e.getId());
        r.setUserId(e.getUser().getUserId());
        r.setCustomerId(e.getCustomer().getCustomerId());
        r.setServiceItemId(e.getService().getId());
        r.setContent(e.getContent());
        r.setNote(e.getNote());
        r.setCreatedAt(e.getCreatedAt());
        r.setUpdatedAt(e.getUpdatedAt());
        return r;
    }
}

