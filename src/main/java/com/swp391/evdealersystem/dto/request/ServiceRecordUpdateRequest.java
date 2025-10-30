package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServiceRecordUpdateRequest {
    @Size(min=5, max=5000) private String content;
    @Size(max=2000) private String note;
}


