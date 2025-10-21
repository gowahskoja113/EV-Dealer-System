package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.CustomerStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private Long customerId;
    private Long vehicleId;
    private String vehicleModel;
    private String name;
    private String phoneNumber;
    private String interestVehicle;
    private CustomerStatus status;
    private Long assignedSalesId;
    private String assignedSalesName;

}
