package com.swp391.evdealersystem.dto.request;

import com.swp391.evdealersystem.enums.CustomerStatus;
import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotBlank(message = "Name is required")
    @Size(max = 120, message = "Name cannot exceed 120 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Size(max = 32, message = "Phone number cannot exceed 32 characters")
    private String phoneNumber;

    @Size(max = 255, message = "Interest vehicle info too long")
    private String interestVehicle;

    @NotNull(message = "Status is required")
    private CustomerStatus status;

    @Positive(message = "Assigned sales ID must be positive")
    private Long assignedSalesId;
}
