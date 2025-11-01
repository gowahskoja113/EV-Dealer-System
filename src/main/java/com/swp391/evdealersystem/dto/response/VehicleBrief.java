package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.VehicleStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class VehicleBrief {
    private Long vehicleId;
    private String imageUrl;
    private VehicleStatus status;
    private OffsetDateTime holdUntil;
    private boolean selectableNow;
}
