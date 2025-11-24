package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.response.VehicleSerialResponse;
import com.swp391.evdealersystem.entity.VehicleSerial;
import org.springframework.stereotype.Component;

@Component
public class VehicleSerialMapper {

    public VehicleSerialResponse toResponse(VehicleSerial entity) {
        if (entity == null) return null;

        return new VehicleSerialResponse(
                entity.getVin(),
                entity.getStatus(),
                entity.getHoldUntil()
        );
    }
}