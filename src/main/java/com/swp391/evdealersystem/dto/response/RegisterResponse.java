package com.swp391.evdealersystem.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String role;

    public RegisterResponse(String name, String email,
                            String phoneNumber, String address, String role) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }

    public RegisterResponse() {}
}