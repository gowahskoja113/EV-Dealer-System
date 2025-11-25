package com.swp391.evdealersystem.service;

import org.springframework.scheduling.annotation.Async;

public interface MailService {
    void sendWelcomeEmail(String toEmail, String userName, String storeName, String roleMessage);

    @Async
    void sendResetPasswordEmail(String toEmail, String userName, String DealerShip, String resetLink);

    void sendAdminWelcomeEmail(String toEmail);

}
