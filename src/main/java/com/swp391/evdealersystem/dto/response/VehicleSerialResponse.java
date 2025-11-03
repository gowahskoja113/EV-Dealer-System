package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleSerialResponse {
    private String vin;
    private VehicleStatus status;
    private OffsetDateTime holdUntil;
}