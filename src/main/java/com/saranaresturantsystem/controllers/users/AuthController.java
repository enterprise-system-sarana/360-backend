package com.saranaresturantsystem.controllers.users;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.saranaresturantsystem.dto.request.users.*;
import com.saranaresturantsystem.dto.response.users.AuthResponse;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.services.users.AuthService;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .status(HttpStatus.CREATED)
                .message("Register success")
                .payload(authService.register(request, httpRequest))
                .Instant(Instant.now())
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Login success")
                .payload(authService.login(request, httpRequest))
                .Instant(Instant.now())
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request, HttpServletRequest httpRequest) {
        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Token refreshed")
                .payload(authService.refresh(request.getRefreshToken(), httpRequest))
                .Instant(Instant.now())
                .build();
    }

    @PostMapping("/logout")
    public ApiResponse<Map<String, String>> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return ApiResponse.<Map<String, String>>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Logout success")
                .payload(Map.of("status", "ok"))
                .Instant(Instant.now())
                .build();
    }

    @PostMapping("/logout-all/{userId}")
    public ApiResponse<Map<String, String>> logoutAll(@PathVariable Long userId) {
        authService.logoutAll(userId);
        return ApiResponse.<Map<String, String>>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Logout all success")
                .payload(Map.of("status", "ok"))
                .Instant(Instant.now())
                .build();
    }

    /* Commented out as these features are not yet implemented in the service layer
    @PostMapping("/social-login")
    public ApiResponse<AuthResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request, HttpServletRequest httpRequest) {
        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Social login success")
                .payload(authService.socialLogin(request, httpRequest))
                .Instant(Instant.now())
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Map<String, String>> forgotPassword(@RequestParam String emailOrUsername) {
        authService.forgotPassword(emailOrUsername);
        return ApiResponse.<Map<String, String>>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Password reset token generated and sent")
                .payload(Map.of("status", "sent"))
                .Instant(Instant.now())
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ApiResponse.<Map<String, String>>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Password reset successfully")
                .payload(Map.of("status", "ok"))
                .Instant(Instant.now())
                .build();
    }

    @PostMapping("/change-password")
    public ApiResponse<Map<String, String>> changePassword(java.security.Principal principal, @Valid @RequestBody ChangePasswordRequest request) {
        if (principal == null) {
            return ApiResponse.<Map<String, String>>builder()
                    .success(false)
                    .status(HttpStatus.UNAUTHORIZED)
                    .message("User is not authenticated")
                    .Instant(Instant.now())
                    .build();
        }
        authService.changePassword(principal.getName(), request.getCurrentPassword(), request.getNewPassword());
        return ApiResponse.<Map<String, String>>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Password changed successfully")
                .payload(Map.of("status", "ok"))
                .Instant(Instant.now())
                .build();
    }

    @PostMapping("/verify-email")
    public ApiResponse<Map<String, String>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ApiResponse.<Map<String, String>>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Email verified successfully")
                .payload(Map.of("status", "ok"))
                .Instant(Instant.now())
                .build();
    }

    @PostMapping("/resend-verification")
    public ApiResponse<Map<String, String>> resendVerification(@RequestParam String emailOrUsername) {
        authService.resendVerificationEmail(emailOrUsername);
        return ApiResponse.<Map<String, String>>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Verification email token generated and sent")
                .payload(Map.of("status", "sent"))
                .Instant(Instant.now())
                .build();
    }
    */
}
