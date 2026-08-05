package com.saranaresturantsystem.services.interfaces.users;

import com.saranaresturantsystem.dto.request.users.LoginRequest;
import com.saranaresturantsystem.dto.request.users.RegisterRequest;
import com.saranaresturantsystem.dto.response.users.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;


public interface AuthService {
    AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest);
    AuthResponse login(LoginRequest request, HttpServletRequest httpRequest);
    AuthResponse refresh(String refreshToken, HttpServletRequest httpRequest);
    void logout(String refreshToken);
    void logoutAll(Long userId);
    void forgotPassword(String emailOrUsername);
    void resetPassword(String token, String newPassword);
    void changePassword(String username, String currentPassword, String newPassword);
    void verifyEmail(String token);
    void resendVerificationEmail(String emailOrUsername);
}
