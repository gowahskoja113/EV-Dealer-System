package com.swp391.evdealersystem.service;

import com.swp391.evdealersystem.entity.PasswordResetToken;
import com.swp391.evdealersystem.entity.User;
import com.swp391.evdealersystem.repository.DealershipRepository;
import com.swp391.evdealersystem.repository.PasswordResetTokenRepository;
import com.swp391.evdealersystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final DealershipRepository dealershipRepo;

    // link FE reset password
    private final String FE_RESET_URL = "http://localhost:3000/reset-password?token=";

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // xoá token cũ nếu có
        tokenRepo.deleteByUser_UserId(user.getUserId());

        // tạo token mới
        String token = UUID.randomUUID().toString().replace("-", "");
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(token);
        prt.setUser(user);
        prt.setExpiryAt(LocalDateTime.now().plusMinutes(15));

        tokenRepo.save(prt);

        String DealerShip = dealershipRepo.findDefaultDealerShip()
                .orElse("EV Dealer Store");

        String resetLink = FE_RESET_URL + token;

        mailService.sendResetPasswordEmail(
                user.getEmail(),
                user.getName(),
                DealerShip,
                resetLink
        );
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));

        if (prt.isExpired()) {
            throw new RuntimeException("Token đã hết hạn");
        }

        User user = prt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // dùng xong xoá luôn
        tokenRepo.delete(prt);
    }
}
