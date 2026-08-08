package com.saranaresturantsystem.services.impl.users;

import com.saranaresturantsystem.services.interfaces.users.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail-from:noreply@sarana.com}")
    private String mailFrom;

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String username, String token) {
        String resetUrl = "http://localhost:5173/reset-password?token=" + token;
        log.info("Sending password reset email to [{}], reset link: {}", toEmail, resetUrl);

        if (mailSender == null || toEmail == null || toEmail.isBlank()) {
            log.warn("JavaMailSender is disabled or email is empty. Password reset token for [{}] is: {}", username, token);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");

            String content = "<h3>Hello " + username + ",</h3>"
                    + "<p>You requested a password reset for your account.</p>"
                    + "<p>Please click the link below to reset your password. This link is valid for 1 hour:</p>"
                    + "<p><a href=\"" + resetUrl + "\">Reset Password</a></p>"
//                    + "<p>Or copy and paste this token into your application: <strong>" + token + "</strong></p>"
                    + "<br/><p>If you did not request this, please ignore this email.</p>";

            helper.setText(content, true);
            mailSender.send(message);
            log.info("Password reset email sent successfully to [{}]", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to [{}]: {}", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendEmailVerification(String toEmail, String username, String token) {
        log.info("Sending email verification to [{}], token: {}", toEmail, token);

        if (mailSender == null || toEmail == null || toEmail.isBlank()) {
            log.warn("JavaMailSender is disabled or email is empty. Verification token for [{}] is: {}", username, token);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(toEmail);
            helper.setSubject("Account Email Verification");

            String content = "<h3>Hello " + username + ",</h3>"
                    + "<p>Thank you for registering. Your verification token is:</p>"
                    + "<p><strong>" + token + "</strong></p>";

            helper.setText(content, true);
            mailSender.send(message);
            log.info("Verification email sent successfully to [{}]", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to [{}]: {}", toEmail, e.getMessage());
        }
    }
}
