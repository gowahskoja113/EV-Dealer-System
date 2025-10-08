package com.swp391.evdealersystem.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelRequest {
    private String modelCode; // mã model (VD: TESLA-M3, NISSAN-LF)
    private String name;      // tên model (VD: Model 3, Leaf, Ioniq 6)
}
