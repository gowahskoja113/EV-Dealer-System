package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.RoleRequest;
import com.swp391.evdealersystem.dto.response.RoleResponse;
import com.swp391.evdealersystem.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public RoleResponse create(@RequestBody RoleRequest req) {
        return roleService.createRole(req);
    }

    @PutMapping
    public RoleResponse update(@RequestBody RoleRequest req) {
        return roleService.updateRole(req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        roleService.deleteRole(id);
    }

    @GetMapping("/{id}")
    public RoleResponse getById(@PathVariable int id) {
        return roleService.getRoleById(id);
    }

    @GetMapping
    public List<RoleResponse> getAll() {
        return roleService.getAllRoles();
    }
}
