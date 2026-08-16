package com.saranaresturantsystem.services.impl.users;

import com.saranaresturantsystem.config.security.JwtService;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.users.LoginRequest;
import com.saranaresturantsystem.dto.request.users.RegisterRequest;
import com.saranaresturantsystem.dto.response.users.AuthResponse;
import com.saranaresturantsystem.entities.users.*;
import com.saranaresturantsystem.execption.ApiException;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.repository.users.RefreshTokenRepository;
import com.saranaresturantsystem.repository.users.RoleRepository;
import com.saranaresturantsystem.repository.users.UserRepository;
import com.saranaresturantsystem.repository.users.VerificationTokenRepository;
import com.saranaresturantsystem.services.interfaces.users.AuthService;
import com.saranaresturantsystem.services.interfaces.users.EmailService;
import com.saranaresturantsystem.utils.PasswordValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    @Value("${app.jwt.refresh-expiration-seconds}")
    private long refreshExpirationSeconds;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {

        if (!request.password().equals(request.confirmPassword())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password and confirm password do not match");
        }

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (request.email() != null) {
            if (!request.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid email format");
            }
            if (userRepository.findByEmail(request.email()).isPresent()) {
                throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
            }
        }
        if (request.phone() != null && !request.phone().isBlank()) {
            if (!request.phone().matches("^\\+?[0-9]{8,15}$")) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid phone number format");
            }
        }

        if (!PasswordValidator.isValid(request.password())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, PasswordValidator.getRequirementsMessage());
        }

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setIsActive(Constants.STATUS_ACTIVE);
        user.setIsLocked(false);
        user.setIsVerified(false);
        user.setFailedLoginAttempts(0);
        // Assign default ROLE_STAFF
        Role defaultRole = roleRepository.findByCode("ROLE_USER")
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Default role 'USERS' not found in database"));
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().add(defaultRole);
        User savedUser = userRepository.save(user);
        VerificationToken token = generateAndSaveToken(savedUser, "EMAIL_VERIFICATION", 24);
        emailService.sendEmailVerification(savedUser.getEmail(), savedUser.getUsername(), token.getToken());
        String accessToken = jwtService.generateAccessToken(savedUser);
        RefreshToken refreshToken = createRefreshToken(savedUser, httpRequest);
        return getAuthResponse(savedUser, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        User user = userRepository.findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (Boolean.TRUE.equals(user.getIsLocked()) || user.getDeletedAt() != null) {
            throw new ApiException(HttpStatus.FORBIDDEN, "User is inactive or locked");
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            user.setFailedLoginAttempts((user.getFailedLoginAttempts() == null ? 0 : user.getFailedLoginAttempts()) + 1);
            userRepository.save(user);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = createRefreshToken(user, httpRequest);
        return getAuthResponse(user, accessToken, refreshToken);
    }

    private AuthResponse getAuthResponse(User user, String accessToken, RefreshToken refreshToken) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getCode)
                .toList();
        List<String> permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getCode)
                .distinct()
                .toList();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessExpirationSeconds())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .permissions(permissions)
//                .storeId(user.getStore() != null ? user.getStore().getId() : null)
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refresh(String refreshTokenValue, HttpServletRequest httpRequest) {
        RefreshToken existing = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Refresh token not found"));

        if (Boolean.TRUE.equals(existing.getIsRevoked()) || existing.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
        }

        User user = existing.getUser();
        revoke(existing);
        RefreshToken rotated = createRefreshToken(user, httpRequest);
        String accessToken = jwtService.generateAccessToken(user);
        return getAuthResponse(user, accessToken, rotated);
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue).ifPresent(this::revoke);
    }

    @Override
    @Transactional
    public void logoutAll(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        List<RefreshToken> tokens = refreshTokenRepository.findByUserAndIsRevokedFalse(user);
        tokens.forEach(this::revoke);
        refreshTokenRepository.saveAll(tokens);
    }

    private VerificationToken generateAndSaveToken(User user, String type, int hoursToExpire) {
        VerificationToken token = new VerificationToken();
        token.setUser(user);
        token.setType(type);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(hoursToExpire));
        return verificationTokenRepository.save(token);
    }

    private RefreshToken createRefreshToken(User user, HttpServletRequest request) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(generateSecureToken());
        refreshToken.setIpAddress(extractIp(request));
        refreshToken.setUserAgent(request != null ? request.getHeader("User-Agent") : null);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshExpirationSeconds));
        refreshToken.setIsRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }

    private void revoke(RefreshToken token) {
        token.setIsRevoked(true);
        token.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(token);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String extractIp(HttpServletRequest request) {
        if (request == null) return null;
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @Override
    @Transactional
    public void forgotPassword(String emailOrUsername) {
        User user = userRepository.findByUsernameOrEmail(emailOrUsername, emailOrUsername)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No account found with that email or username"));

        // Delete any existing password reset tokens for this user safely
        verificationTokenRepository.deleteByUserAndType(user, "PASSWORD_RESET");

        // Generate a new password reset token (valid for 1 hour)
        VerificationToken token = generateAndSaveToken(user, "PASSWORD_RESET", 1);

        // Send email with the reset token/link
        emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), token.getToken());
        log.info("Password reset token generated for user [{}]: {}", user.getUsername(), token.getToken());
    }

    @Override
    public boolean verifyResetToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndType(token, "PASSWORD_RESET")
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid or non-existent password reset token"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password reset token has expired");
        }
        return true;
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndType(token, "PASSWORD_RESET")
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired password reset token"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password reset token has expired");
        }

        if (!PasswordValidator.isValid(newPassword)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, PasswordValidator.getRequirementsMessage());
        }

        User user = verificationToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        // Revoke all refresh tokens for security
        List<RefreshToken> activeTokens = refreshTokenRepository.findByUserAndIsRevokedFalse(user);
        activeTokens.forEach(this::revoke);

        // Delete the used verification token
        verificationTokenRepository.delete(verificationToken);

        log.info("Password reset successfully for user [{}]", user.getUsername());
    }

    @Override
    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        if (!PasswordValidator.isValid(newPassword)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, PasswordValidator.getRequirementsMessage());
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password changed successfully for user [{}]", user.getUsername());
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndType(token, "EMAIL_VERIFICATION")
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Invalid or expired verification token"));

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new ApiException(HttpStatus.BAD_REQUEST, "Verification token has expired");
        }

        User user = verificationToken.getUser();
        user.setIsVerified(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);

        log.info("Email verified successfully for user [{}]", user.getUsername());
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String emailOrUsername) {
        User user = userRepository.findByUsernameOrEmail(emailOrUsername, emailOrUsername)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "No account found with that email or username"));

        if (Boolean.TRUE.equals(user.getIsVerified())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is already verified");
        }

        // Delete any existing email verification tokens safely
        verificationTokenRepository.deleteByUserAndType(user, "EMAIL_VERIFICATION");

        VerificationToken token = generateAndSaveToken(user, "EMAIL_VERIFICATION", 24);

        // Send verification email
        emailService.sendEmailVerification(user.getEmail(), user.getUsername(), token.getToken());
        log.info("Verification email token generated for user [{}]: {}", user.getUsername(), token.getToken());
    }
}
