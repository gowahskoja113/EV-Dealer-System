package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class UpdateDeliveryDateRequest {

    @NotNull(message = "Delivery date cannot be null")
    private LocalDate deliveryDate;
}