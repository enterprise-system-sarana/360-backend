package com.saranaresturantsystem.config.security;

import com.saranaresturantsystem.entities.users.Permission;
import com.saranaresturantsystem.entities.users.Role;
import com.saranaresturantsystem.entities.users.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Getter
    @Value("${app.jwt.access-expiration-seconds:900}")
    private long accessExpirationSeconds;

    @Getter
    @Value("${app.jwt.refresh-expiration-seconds:2592000}")
    private long refreshExpirationSeconds;


    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        claims.put("username", user.getUsername());
        // Roles
        List<String> roles = user.getRoles().stream().map(Role::getCode).distinct().toList();
        claims.put("roles", roles);
        // Permissions
        List<String> permissions = user.getRoles().stream().flatMap(role -> role.getPermissions().stream()).map(Permission::getCode).distinct().toList();
        claims.put("permissions", permissions);
        return generateToken(claims, user.getEmail(), accessExpirationSeconds, "access");
    }


    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", user.getId());
        claims.put("type", "refresh");
        return generateToken(claims, user.getEmail(), refreshExpirationSeconds, "refresh");
    }


    private String generateToken(Map<String, Object> claims, String subject, long expirationSeconds, String tokenType) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(expirationSeconds);
        claims.put("type", tokenType);
        return Jwts.builder().claims(claims).subject(subject).issuedAt(Date.from(now)).expiration(Date.from(expiration)).signWith(getSigningKey()).compact();
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    public boolean isAccessTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date()) && "access".equals(claims.get("type", String.class));
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date()) && "refresh".equals(claims.get("type", String.class));
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isTokenValid(String token) {
        return isAccessTokenValid(token);
    }

    public Long extractUserId(String token) {

        Claims claims = extractAllClaims(token);

        return claims.get("uid", Long.class);
    }

    public String extractSubject(String token) {

        return extractAllClaims(token).getSubject();
    }

    public String extractTokenType(String token) {

        return extractAllClaims(token).get("type", String.class);
    }

    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);
    }
}