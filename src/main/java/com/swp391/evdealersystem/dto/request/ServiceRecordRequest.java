package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServiceRecordRequest {
    @NotNull
    private Long userId;
    @NotNull private Long customerId;
    @NotNull private Long serviceId;
    @NotBlank
    @Size(min=5, max=5000) private String content;
    @Size(max=2000) private String note;
}
