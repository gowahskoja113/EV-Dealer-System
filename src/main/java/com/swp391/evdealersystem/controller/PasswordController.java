package com.swp391.evdealersystem.controller;

import com.swp391.evdealersystem.dto.request.ForgotPasswordRequest;
import com.swp391.evdealersystem.dto.request.ResetPasswordRequest;
import com.swp391.evdealersystem.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    // 1) Quên mật khẩu
    @PostMapping("/forgot")
    public ResponseEntity<?> forgot(@Valid @RequestBody ForgotPasswordRequest req) {
        passwordService.forgotPassword(req.getEmail());
        return ResponseEntity.ok("Đã gửi link đặt lại mật khẩu về email.");
    }

    // 2) Đặt lại mật khẩu
    @PostMapping("/reset")
    public ResponseEntity<?> reset(@Valid @RequestBody ResetPasswordRequest req) {
        passwordService.resetPassword(req.getToken(), req.getNewPassword());
        return ResponseEntity.ok("Đổi mật khẩu thành công.");
    }
}
