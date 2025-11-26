package com.swp391.evdealersystem.dto.response;

import com.swp391.evdealersystem.enums.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResponse {
    private Long id;
    private String name;
    private String description;
    private ServiceType serviceType;
}


