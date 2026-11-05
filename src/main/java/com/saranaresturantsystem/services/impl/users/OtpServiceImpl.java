package com.saranaresturantsystem.services.impl.users;

import com.saranaresturantsystem.entities.users.OtpVerification;
import com.saranaresturantsystem.repository.users.OtpVerificationRepository;
import com.saranaresturantsystem.services.interfaces.users.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {
    private final OtpVerificationRepository otpRepository;

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
        sendOtpEmail(email, optCode);
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
        log.info("OTP generated for [{}]: {}", toEmail, otpCode);
    }
}
