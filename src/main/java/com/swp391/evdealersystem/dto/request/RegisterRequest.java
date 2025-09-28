package com.swp391.evdealersystem.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String password;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;

    @JsonAlias({"role", "roleId"})
    private Integer roleId;
}
