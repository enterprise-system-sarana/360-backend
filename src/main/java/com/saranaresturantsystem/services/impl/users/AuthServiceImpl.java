package com.saranaresturantsystem.services.impl.users;

import com.saranaresturantsystem.config.security.JwtService;
import com.saranaresturantsystem.dto.request.users.LoginRequest;
import com.saranaresturantsystem.dto.request.users.RegisterRequest;
import com.saranaresturantsystem.dto.response.users.AuthResponse;
import com.saranaresturantsystem.entities.users.*;
import com.saranaresturantsystem.enums.StatusType;
import com.saranaresturantsystem.execption.ApiException;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.repository.users.RefreshTokenRepository;
import com.saranaresturantsystem.repository.users.RoleRepository;
import com.saranaresturantsystem.repository.users.UserRepository;
import com.saranaresturantsystem.repository.users.VerificationTokenRepository;
import com.saranaresturantsystem.services.interfaces.users.AuthService;
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

    @Value("${app.jwt.refresh-expiration-seconds}")
    private long refreshExpirationSeconds;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {

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
//        if (request.getPhone() != null && !request.getPhone().isBlank()) {
//            if (!request.getPhone().matches("^\\+?[0-9]{8,15}$")) {
//                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid phone number format");
//            }
//
//        }

        if (!PasswordValidator.isValid(request.password())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, PasswordValidator.getRequirementsMessage());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setIsActive("ACTIVE");
        user.setIsLocked(false);
        user.setIsVerified(false);
        user.setFailedLoginAttempts(0);
        // Assign default ROLE_STAFF
        roleRepository.findByCode("ROLE_STAFF").ifPresent(role -> user.getRoles().add(role));
        User savedUser = userRepository.save(user);
        // Auto-generate verification token for email verification
        generateAndSaveToken(savedUser, "EMAIL_VERIFICATION", 24);
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
        token.setExpiresAt(LocalDateTime.now().plusHours(hoursToExpire));
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
}
