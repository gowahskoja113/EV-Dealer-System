package com.swp391.evdealersystem.mapper;

import com.swp391.evdealersystem.dto.request.RoleRequest;
import com.swp391.evdealersystem.dto.response.RoleResponse;
import com.swp391.evdealersystem.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    public Role toEntity(RoleRequest req) {
        if (req == null) return null;
        Role r = new Role();
        if (req.getRoleId() != null) r.setRoleId(req.getRoleId());
        r.setRoleName(req.getRoleName());
        r.setDescription(req.getDescription());
        return r;
    }

    public RoleResponse toResponse(Role r) {
        if (r == null) return null;
        RoleResponse res = new RoleResponse();
        res.setRoleId(r.getRoleId());
        res.setRoleName(r.getRoleName());
        res.setDescription(r.getDescription());
        return res;
    }
}
