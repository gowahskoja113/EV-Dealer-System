package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class ServiceRequest {
    @NotBlank
    private String name;
    @Size(max=2000) private String description;
}