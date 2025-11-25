package com.swp391.evdealersystem.service;

public interface PasswordService {
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
}
