package com.swp391.evdealersystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequest {
    private Integer userId;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    private String phoneNumber;

    @Email
    private String email;

    private String address;

    private Integer roleId;
}
