package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.RoleRequest;
import com.swp391.evdealersystem.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    RoleResponse createRole(RoleRequest req);
    RoleResponse updateRole(RoleRequest req);
    void deleteRole(int id);
    RoleResponse getRoleById(int id);
    List<RoleResponse> getAllRoles();
}
