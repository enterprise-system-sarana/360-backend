package com.saranaresturantsystem.services.interfaces.users;

public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String username, String token);
    void sendEmailVerification(String toEmail, String username, String token);
}
