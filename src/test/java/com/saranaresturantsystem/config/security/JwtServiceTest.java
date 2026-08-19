package com.saranaresturantsystem.config.security;

import com.saranaresturantsystem.entities.users.Permission;
import com.saranaresturantsystem.entities.users.Role;
import com.saranaresturantsystem.entities.users.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Generate a 256-bit secret key encoded in Base64
        byte[] key = new byte[32];
        for (int i = 0; i < 32; i++) {
            key[i] = (byte) (i + 1);
        }
        String base64Secret = Base64.getEncoder().encodeToString(key);

        ReflectionTestUtils.setField(jwtService, "jwtSecret", base64Secret);
        ReflectionTestUtils.setField(jwtService, "accessExpirationSeconds", 900L);
        ReflectionTestUtils.setField(jwtService, "refreshExpirationSeconds", 2592000L);
    }

    @Test
    void testGenerateAccessToken_ValidatesAsAccessToken() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        Role role = new Role();
        role.setCode("ADMIN");
        Permission perm = new Permission();
        perm.setCode("user:read");
        role.setPermissions(Set.of(perm));
        user.setRoles(Set.of(role));

        String accessToken = jwtService.generateAccessToken(user);

        assertNotNull(accessToken);
        assertTrue(jwtService.isAccessTokenValid(accessToken));
        assertTrue(jwtService.isTokenValid(accessToken));
        assertFalse(jwtService.isRefreshTokenValid(accessToken));
        assertEquals("access", jwtService.extractTokenType(accessToken));
        assertEquals("test@example.com", jwtService.extractSubject(accessToken));
    }

    @Test
    void testGenerateRefreshToken_ValidatesAsRefreshToken() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRoles(Set.of());

        String refreshToken = jwtService.generateRefreshToken(user);

        assertNotNull(refreshToken);
        assertTrue(jwtService.isRefreshTokenValid(refreshToken));
        assertFalse(jwtService.isAccessTokenValid(refreshToken));
        assertFalse(jwtService.isTokenValid(refreshToken));
        assertEquals("refresh", jwtService.extractTokenType(refreshToken));
    }
}
