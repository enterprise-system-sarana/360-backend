package com.saranaresturantsystem.services.impl.users;

import com.saranaresturantsystem.entities.users.OtpVerification;
import com.saranaresturantsystem.repository.users.OtpVerificationRepository;
import com.saranaresturantsystem.services.interfaces.users.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {
    private  final OtpVerificationRepository otpRepository;
    private  final JavaMailSender mailSender;

    @Transactional
    @Override
    public void generateAndSendOtp(String email) {
        otpRepository.deleteByEmail(email);
        SecureRandom random = new SecureRandom();
        String optCode = String.format("%06d", random.nextInt(1000000));
        OtpVerification otp = new OtpVerification();
        otp.setEmail(email);
        otp.setOtpCode(optCode);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(3));
        otpRepository.save(otp);
        sendOtpEmail(email , optCode);
    }

    @Transactional
    @Override
    public void verifyOtp(String email, String otpCode) {
        OtpVerification otp = otpRepository.findByEmailAndOtpCode(email, otpCode)
                .orElseThrow(() -> new IllegalArgumentException("លេខ OTP មិនត្រឹមត្រូវទេ!"));

        if (otp.isExpired()) {
            otpRepository.delete(otp);
            throw new IllegalArgumentException("លេខ OTP នេះបានផុតកំណត់ហើយ!");
        }

        otpRepository.delete(otp);
    }

    @Transactional
    @Override
    public void sendOtpEmail(String toEmail, String otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("លេខកូដផ្ទៀងផ្ទាត់ OTP របស់អ្នក");
        message.setText("លេខ OTP របស់អ្នកគឺ៖ " + otpCode + "\n\nលេខកូដនេះមានសុពលភាពត្រឹមតែ ៣ នាទីប៉ុណ្ណោះ។");

        mailSender.send(message);
        log.info("Sent OTP email to {}", toEmail);
    }
}
