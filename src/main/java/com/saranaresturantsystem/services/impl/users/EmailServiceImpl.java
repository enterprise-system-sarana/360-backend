package com.saranaresturantsystem.services.impl.users;

import com.saranaresturantsystem.services.interfaces.users.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${app.reset-password-url:http://localhost:5173/reset-password}")
    private String resetPasswordUrl;

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String username, String token) {
        String separator = resetPasswordUrl.contains("?") ? "&" : "?";
        String resetUrl = resetPasswordUrl + separator + "token=" + token;
        log.info("Password reset requested for [{} ({})], reset link: {}, token: {}", username, toEmail, resetUrl, token);
    }

    @Override
    @Async
    public void sendEmailVerification(String toEmail, String username, String token) {
        log.info("Email verification requested for [{} ({})], token: {}", username, toEmail, token);
    }
}
