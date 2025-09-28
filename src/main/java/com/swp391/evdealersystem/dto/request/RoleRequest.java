package com.swp391.evdealersystem.dto.request;

import lombok.Data;

@Data
public class RoleRequest {
    private Integer roleId;      // dùng cho update
    private String roleName;
    private String description;
}
