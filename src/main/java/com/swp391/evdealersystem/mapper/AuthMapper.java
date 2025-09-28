package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.RegisterRequest;
import com.swp391.evdealersystem.entity.User;
import com.swp391.evdealersystem.dto.response.RegisterResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public User toEntity(RegisterRequest request) {
        User user = new User();
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        return user;
    }

    public RegisterResponse toDto(User user) {
        RegisterResponse response = new RegisterResponse();
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setRole(user.getRole().getRoleName());
        return response;
    }
}