package com.saranaresturantsystem.services.impl.users;

import com.saranaresturantsystem.config.security.JwtService;
import com.saranaresturantsystem.dto.response.users.AuthResponse;
import com.saranaresturantsystem.entities.users.RefreshToken;
import com.saranaresturantsystem.entities.users.User;
import com.saranaresturantsystem.execption.ApiException;
import com.saranaresturantsystem.repository.users.RefreshTokenRepository;
import com.saranaresturantsystem.repository.users.RoleRepository;
import com.saranaresturantsystem.repository.users.UserRepository;
import com.saranaresturantsystem.repository.users.VerificationTokenRepository;
import com.saranaresturantsystem.services.interfaces.users.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

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

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "refreshExpirationSeconds", 86400L);
    }

    @Test
    void testRefresh_Success_GeneratesAccessToken() {
        User user = new User();
        user.setId(10L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setRoles(Set.of());

        RefreshToken existingToken = new RefreshToken();
        existingToken.setId(1L);
        existingToken.setToken("valid-refresh-token");
        existingToken.setUser(user);
        existingToken.setIsRevoked(false);
        existingToken.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(refreshTokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(existingToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateAccessToken(user)).thenReturn("mocked-access-token");
        when(jwtService.getAccessExpirationSeconds()).thenReturn(900L);

        AuthResponse response = authService.refresh("valid-refresh-token", null);

        assertNotNull(response);
        assertEquals("mocked-access-token", response.accessToken());
        assertNotNull(response.refreshToken());
        assertNotEquals("valid-refresh-token", response.refreshToken()); // Should be rotated token

        // Verify that generateAccessToken was invoked (NOT generateRefreshToken)
        verify(jwtService).generateAccessToken(user);
        verify(jwtService, never()).generateRefreshToken(user);
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class)); // 1 for revoking existing, 1 for saving rotated
    }

    @Test
    void testRefresh_ExpiredToken_ThrowsUnauthorized() {
        User user = new User();
        user.setId(10L);

        RefreshToken expiredToken = new RefreshToken();
        expiredToken.setToken("expired-token");
        expiredToken.setUser(user);
        expiredToken.setIsRevoked(false);
        expiredToken.setExpiresAt(LocalDateTime.now().minusMinutes(5));

        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));

        assertThrows(ApiException.class, () -> authService.refresh("expired-token", null));
    }
}
