package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.RoleRequest;
import com.swp391.evdealersystem.dto.response.RoleResponse;
import com.swp391.evdealersystem.entity.Role;
import com.swp391.evdealersystem.mapper.RoleMapper;
import com.swp391.evdealersystem.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public RoleResponse createRole(RoleRequest req) {
        Role role = roleMapper.toEntity(req);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponse updateRole(RoleRequest req) {
        Role role = roleMapper.toEntity(req);
        return roleMapper.toResponse(roleRepository.save(role));
    }

    @Override
    public void deleteRole(int id) {
        roleRepository.deleteById(id);
    }

    @Override
    public RoleResponse getRoleById(int id) {
        return roleRepository.findById(id)
                .map(roleMapper::toResponse)
                .orElse(null);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }
}
