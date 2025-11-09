package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.CustomerRequest;
import com.swp391.evdealersystem.dto.response.CustomerResponse;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import com.swp391.evdealersystem.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request,
                             User assignedSales) {
        if (request == null) return null;
        Customer entity = new Customer();
        entity.setName(request.getName());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setAddress(request.getAddress());
        entity.setNote(request.getNote());
        entity.setStatus(request.getStatus());
        entity.setAssignedSales(assignedSales);
        return entity;
    }

    public void updateEntity(Customer entity,
                             CustomerRequest request,
                             User assignedSales) {
        if (entity == null || request == null) return;
        entity.setName(request.getName());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setAddress(request.getAddress());
        entity.setNote(request.getNote());
        entity.setStatus(request.getStatus());
        entity.setAssignedSales(assignedSales);
    }


    public CustomerResponse toResponse(Customer entity) {
        if (entity == null) return null;

        Long assignedSalesId = null;
        String assignedSalesName = null;
        if (entity.getAssignedSales() != null) {
            var u = entity.getAssignedSales();
            assignedSalesId = u.getUserId();
            String name = u.getName();
            if (name != null) {
                name = name.trim();
                if (!name.isEmpty()) assignedSalesName = name;
            }
        }

        return CustomerResponse.builder()
                .customerId(entity.getCustomerId())
                .name(entity.getName())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .note(entity.getNote())
                .status(entity.getStatus())
                .assignedSalesId(assignedSalesId)
                .assignedSalesName(assignedSalesName)
                .build();
    }
}
