package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.UserRequest;
import com.swp391.evdealersystem.dto.response.UserResponse;
import com.swp391.evdealersystem.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest req) {
        if (req == null) return null;
        User u = new User();
        u.setUserId(req.getUserId() != null ? req.getUserId() : 0);
        u.setPassword(req.getPassword());
        u.setName(req.getName());
        u.setPhoneNumber(req.getPhoneNumber());
        u.setEmail(req.getEmail());
        u.setAddress(req.getAddress());
        return u;
    }

    public UserResponse toResponse(User u) {
        if (u == null) return null;
        UserResponse r = new UserResponse();
        r.setUserId(u.getUserId());
        r.setName(u.getName());
        r.setPhoneNumber(u.getPhoneNumber());
        r.setEmail(u.getEmail());
        r.setAddress(u.getAddress());
        if (u.getRole() != null) {
            r.setRoleName(u.getRole().getRoleName());
        }
        return r;
    }
}
