package com.swp391.evdealersystem.dto.response;

import lombok.Data;

@Data
public class UserResponse {
    private Long userId;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private String roleName; // lấy từ Role
}
