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
                             ElectricVehicle vehicle,
                             User assignedSales) {
        if (request == null || vehicle == null) return null;

        Customer entity = new Customer();
        entity.setVehicle(vehicle);
        entity.setName(request.getName());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setInterestVehicle(request.getInterestVehicle());
        entity.setStatus(request.getStatus());
        entity.setAssignedSales(assignedSales);
        return entity;
    }

    public void updateEntity(Customer entity,
                             CustomerRequest request,
                             ElectricVehicle vehicle,
                             User assignedSales) {
        if (entity == null || request == null) return;
        if (vehicle != null) entity.setVehicle(vehicle);
        entity.setName(request.getName());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setInterestVehicle(request.getInterestVehicle());
        entity.setStatus(request.getStatus());
        entity.setAssignedSales(assignedSales);
    }


    public CustomerResponse toResponse(Customer entity) {
        if (entity == null) return null;

        String vehicleModelStr = null;
        if (entity.getVehicle() != null && entity.getVehicle().getModel() != null) {
            var m = entity.getVehicle().getModel();
            String brand = m.getBrand() != null ? m.getBrand().trim() : "";
            String code  = m.getModelCode() != null ? m.getModelCode().trim() : "";
            vehicleModelStr = (brand + " " + code).trim();
            if (vehicleModelStr.isEmpty()) vehicleModelStr = null;
        }

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
                .vehicleId(entity.getVehicle() != null ? entity.getVehicle().getVehicleId() : null)
                .vehicleModel(vehicleModelStr)
                .name(entity.getName())
                .phoneNumber(entity.getPhoneNumber())
                .interestVehicle(entity.getInterestVehicle())
                .status(entity.getStatus())
                .assignedSalesId(assignedSalesId)
                .assignedSalesName(assignedSalesName)
                .build();
    }


}
