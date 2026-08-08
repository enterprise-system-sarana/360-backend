package com.saranaresturantsystem.services.interfaces.users;

public interface OtpService {
    void generateAndSendOtp(String email);
    void verifyOtp(String email , String otpCode);
    void sendOtpEmail(String toEmail , String otpCode);
}
