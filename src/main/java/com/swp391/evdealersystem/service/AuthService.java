package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.dto.request.AuthRequest;
import com.swp391.evdealersystem.dto.request.RegisterRequest;
import com.swp391.evdealersystem.dto.response.AuthResponse;
import com.swp391.evdealersystem.dto.response.RegisterResponse;
import com.swp391.evdealersystem.entity.Role;
import com.swp391.evdealersystem.entity.User;
import com.swp391.evdealersystem.mapper.AuthMapper;
import com.swp391.evdealersystem.repository.RoleRepository;
import com.swp391.evdealersystem.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JpaUserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       AuthMapper authMapper,
                       PasswordEncoder passwordEncoder,
                       JpaUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.authMapper = authMapper;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already used");
        }

        User user = authMapper.toEntity(req);

        Integer roleId = (req.getRoleId() != null) ? req.getRoleId() : 1;

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id " + roleId));

        user.setRole(role);

        user.setPassword(passwordEncoder.encode(req.getPassword()));

        userRepository.save(user);

        return new RegisterResponse(
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                role.getRoleName()
        );
    }

    public AuthResponse login(AuthRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(req.getEmail());

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, req.getEmail());
    }
}
