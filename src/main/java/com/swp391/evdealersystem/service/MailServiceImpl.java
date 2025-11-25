package com.swp391.evdealersystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    @Async
    public void sendWelcomeEmail(String toEmail, String userName, String storeName, String roleMessage) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(toEmail);
        msg.setSubject("Chào mừng bạn đến với " + storeName);

        msg.setText("""
            Xin chào %s,

            Chào mừng bạn đến với cửa hàng %s!
            %s

            Chúc bạn trải nghiệm hệ thống vui vẻ.

            Trân trọng,
            %s
            """.formatted(userName, storeName, roleMessage, storeName));

        mailSender.send(msg);
    }


    @Async
    @Override
    public void sendResetPasswordEmail(String toEmail, String userName, String DealerShip, String resetLink) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(toEmail);
        msg.setSubject("Đặt lại mật khẩu - " + DealerShip);

        msg.setText("""
            Xin chào %s,

            Bạn vừa yêu cầu đặt lại mật khẩu tại %s.
            Nhấn vào link dưới đây để tạo mật khẩu mới (link có hiệu lực 15 phút):

            %s

            Nếu không phải bạn yêu cầu, hãy bỏ qua email này.

            Trân trọng,
            %s
            """.formatted(userName, DealerShip, resetLink, DealerShip));

        mailSender.send(msg);
    }
    @Override
    @Async
    public void sendAdminWelcomeEmail(String toEmail) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(toEmail);
        msg.setSubject("Tài khoản ADMIN đã được tạo");

        msg.setText("""
            Tài khoản ADMIN của bạn đã được tạo thành công.
            Bạn có thể đăng nhập để quản trị hệ thống.

            Trân trọng,
            EV Dealer System
            """);

        mailSender.send(msg);
    }
}
