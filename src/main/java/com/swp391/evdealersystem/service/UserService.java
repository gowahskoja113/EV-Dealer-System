package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.UserRequest;
import com.swp391.evdealersystem.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse create(UserRequest request);
    List<UserResponse> getAll();
    UserResponse getById(Integer id);
    UserResponse update(Integer id, UserRequest request);
    void delete(Integer id);
}
