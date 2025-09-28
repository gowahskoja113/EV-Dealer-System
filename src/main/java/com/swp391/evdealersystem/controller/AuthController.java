package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.AuthRequest;
import com.swp391.evdealersystem.dto.request.RegisterRequest;
import com.swp391.evdealersystem.dto.response.AuthResponse;
import com.swp391.evdealersystem.dto.response.RegisterResponse;
import com.swp391.evdealersystem.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }
}
