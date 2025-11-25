package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.UserRequest;
import com.swp391.evdealersystem.dto.response.UserResponse;
import com.swp391.evdealersystem.entity.Role;
import com.swp391.evdealersystem.entity.User;
import com.swp391.evdealersystem.mapper.UserMapper;
import com.swp391.evdealersystem.repository.DealershipRepository;
import com.swp391.evdealersystem.repository.RoleRepository;
import com.swp391.evdealersystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final MailService mailService;
    private final DealershipRepository dealershipRepository;

    @Override
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // DTO → Entity
        User user = userMapper.toEntity(request);
        user.setUserId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Gán role
        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
        }

        // Save user
        User saved = userRepository.save(user);

        // Lấy role từ DB, ví dụ: ROLE_ADMIN
        String rawRoleName = saved.getRole() != null
                ? saved.getRole().getRoleName()
                : "ROLE_STAFF";

        // Chuyển ROLE_ADMIN → ADMIN
        String roleName = rawRoleName.replace("ROLE_", "").toUpperCase();

        // ========== CASE ADMIN ==========
        if ("ADMIN".equals(roleName)) {

            mailService.sendAdminWelcomeEmail(saved.getEmail());

        } else {
            // ========== CASE MANAGER / STAFF ==========

            // Lấy tên cửa hàng
            String storeName = dealershipRepository.findDefaultDealerShip()
                    .orElse("EV Dealer Store");

            // ROLE MESSAGE
            String roleMessage = switch (roleName) {
                case "MANAGER" -> "Bạn đã được cấp quyền quản lý cửa hàng/đại lý.";
                case "STAFF" -> "Bạn đã được thêm vào đội ngũ nhân viên của cửa hàng.";
                default -> "Tài khoản của bạn đã được tạo thành công.";
            };

            mailService.sendWelcomeEmail(
                    saved.getEmail(),
                    saved.getName(),
                    storeName,
                    roleMessage
            );
        }

        return userMapper.toResponse(saved);
    }

    @Override
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse update(Long id, UserRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null) existing.setName(request.getName());
        if (request.getPhoneNumber() != null) existing.setPhoneNumber(request.getPhoneNumber());
        if (request.getEmail() != null) existing.setEmail(request.getEmail());
        if (request.getAddress() != null) existing.setAddress(request.getAddress());

        if (request.getPassword() != null) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            existing.setRole(role);
        }

        User saved = userRepository.save(existing);
        return userMapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
