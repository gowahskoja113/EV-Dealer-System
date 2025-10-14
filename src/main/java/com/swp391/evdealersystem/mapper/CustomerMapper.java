package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.CustomerRequest;
import com.swp391.evdealersystem.dto.response.CustomerResponse;
import com.swp391.evdealersystem.entity.Customer;
import com.swp391.evdealersystem.entity.ElectricVehicle;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    // Convert từ request → entity
    public Customer toEntity(CustomerRequest request, ElectricVehicle vehicle) {
        if (request == null || vehicle == null) return null;

        Customer entity = new Customer();
        entity.setVehicle(vehicle);
        entity.setName(request.getName());
        entity.setPhoneNumber(request.getPhoneNumber());
        entity.setInterestVehicle(request.getInterestVehicle());
        entity.setStatus(request.getStatus());

        return entity;
    }

    public CustomerResponse toResponse(Customer entity) {
        if (entity == null) return null;

        String vehicleModelStr = null;
        if (entity.getVehicle() != null && entity.getVehicle().getModel() != null) {
            var m = entity.getVehicle().getModel();
            // Ví dụ: "VinFast VF8" (brand + modelCode)
            String brand = m.getBrand() != null ? m.getBrand().trim() : "";
            String code  = m.getModelCode() != null ? m.getModelCode().trim() : "";
            vehicleModelStr = (brand + " " + code).trim();
            if (vehicleModelStr.isEmpty()) vehicleModelStr = null;
        }

        return CustomerResponse.builder()
                .customerId(entity.getCustomerId())
                .vehicleId(entity.getVehicle() != null ? entity.getVehicle().getVehicleId() : null)
                .vehicleModel(vehicleModelStr)
                .name(entity.getName())
                .phoneNumber(entity.getPhoneNumber())
                .interestVehicle(entity.getInterestVehicle())
                .status(entity.getStatus())
                .build();
    }

}
