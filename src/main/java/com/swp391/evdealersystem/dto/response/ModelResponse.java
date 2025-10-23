package com.swp391.evdealersystem.dto.response;

import lombok.Data;

@Data
public class ModelResponse {
    private Long modelId;
    private String modelCode;
    private String brand;
    private String color;
    private Integer productionYear;
}
