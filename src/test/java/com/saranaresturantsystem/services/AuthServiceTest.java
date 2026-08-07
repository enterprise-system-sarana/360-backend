package com.saranaresturantsystem.services;

import com.saranaresturantsystem.config.security.JwtService;
import com.saranaresturantsystem.entities.users.RefreshToken;
import com.saranaresturantsystem.entities.users.User;
import com.saranaresturantsystem.entities.users.VerificationToken;
import com.saranaresturantsystem.execption.ApiException;
import com.saranaresturantsystem.repository.users.RefreshTokenRepository;
import com.saranaresturantsystem.repository.users.RoleRepository;
import com.saranaresturantsystem.repository.users.UserRepository;
import com.saranaresturantsystem.repository.users.VerificationTokenRepository;
import com.saranaresturantsystem.services.impl.users.AuthServiceImpl;
import com.saranaresturantsystem.services.interfaces.users.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashed_password");
    }

    @Test
    void forgotPassword_Success() {
        when(userRepository.findByUsernameOrEmail("test@example.com", "test@example.com"))
                .thenReturn(Optional.of(testUser));
        when(verificationTokenRepository.save(any(VerificationToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        authService.forgotPassword("test@example.com");

        verify(verificationTokenRepository).deleteByUserAndType(testUser, "PASSWORD_RESET");
        verify(verificationTokenRepository).save(any(VerificationToken.class));
        verify(emailService).sendPasswordResetEmail(eq("test@example.com"), eq("testuser"), anyString());
    }

    @Test
    void forgotPassword_UserNotFound_ThrowsException() {
        when(userRepository.findByUsernameOrEmail("unknown@example.com", "unknown@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> authService.forgotPassword("unknown@example.com"));
        verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    void verifyResetToken_Success() {
        VerificationToken token = new VerificationToken();
        token.setToken("valid-token");
        token.setType("PASSWORD_RESET");
        token.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(verificationTokenRepository.findByTokenAndType("valid-token", "PASSWORD_RESET"))
                .thenReturn(Optional.of(token));

        assertTrue(authService.verifyResetToken("valid-token"));
    }

    @Test
    void verifyResetToken_Expired_ThrowsException() {
        VerificationToken token = new VerificationToken();
        token.setToken("expired-token");
        token.setType("PASSWORD_RESET");
        token.setExpiresAt(LocalDateTime.now().minusMinutes(5));

        when(verificationTokenRepository.findByTokenAndType("expired-token", "PASSWORD_RESET"))
                .thenReturn(Optional.of(token));

        assertThrows(ApiException.class, () -> authService.verifyResetToken("expired-token"));
    }

    @Test
    void resetPassword_Success() {
        VerificationToken token = new VerificationToken();
        token.setToken("valid-token");
        token.setType("PASSWORD_RESET");
        token.setUser(testUser);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));

        RefreshToken activeToken = new RefreshToken();
        activeToken.setUser(testUser);

        when(verificationTokenRepository.findByTokenAndType("valid-token", "PASSWORD_RESET"))
                .thenReturn(Optional.of(token));
        when(passwordEncoder.encode("NewStrongP@ss123")).thenReturn("new_hashed_password");
        when(refreshTokenRepository.findByUserAndIsRevokedFalse(testUser))
                .thenReturn(List.of(activeToken));

        authService.resetPassword("valid-token", "NewStrongP@ss123");

        verify(userRepository).save(testUser);
        verify(verificationTokenRepository).delete(token);
        verify(refreshTokenRepository).save(activeToken);
        assertEquals("new_hashed_password", testUser.getPasswordHash());
        assertNotNull(testUser.getPasswordChangedAt());
    }

    @Test
    void resetPassword_WeakPassword_ThrowsException() {
        VerificationToken token = new VerificationToken();
        token.setToken("valid-token");
        token.setType("PASSWORD_RESET");
        token.setUser(testUser);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(verificationTokenRepository.findByTokenAndType("valid-token", "PASSWORD_RESET"))
                .thenReturn(Optional.of(token));

        assertThrows(ApiException.class, () -> authService.resetPassword("valid-token", "weak"));
        verify(userRepository, never()).save(any());
    }
}
