package com.saranaresturantsystem.config.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {

            // Get Authorization Header
            String authHeader = request.getHeader("Authorization");

            log.info("Authorization Header: {}", authHeader);

            // Check if token exists
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Remove "Bearer "
            String token = authHeader.substring(7);

            log.info("Extracted Token: {}", token);

            // Validate token
            if (!jwtService.isTokenValid(token)) {
                log.warn("Invalid JWT Token");
                filterChain.doFilter(request, response);
                return;
            }

            // Extract claims
            Claims claims = jwtService.extractAllClaims(token);

            String username = claims.getSubject();

            // Extract User ID safely
            // Number uid = claims.get("uid", Number.class);
            // Long userId = uid != null ? uid.longValue() : null;

            // Extract Roles and Permissions list
            java.util.List<?> roles = claims.get("roles", java.util.List.class);
            java.util.List<?> permissions = claims.get("permissions", java.util.List.class);

            log.info("Username: {}", username);
            log.info("Roles: {}", roles);
            log.info("Permissions: {}", permissions);

            // Create authorities
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

            if (roles != null) {
                for (Object roleObj : roles) {
                    if (roleObj != null) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleObj.toString()));
                    }
                }
            }

            if (permissions != null) {
                for (Object permObj : permissions) {
                    if (permObj != null) {
                        authorities.add(new SimpleGrantedAuthority(permObj.toString()));
                    }
                }
            }

            // Create Authentication Object
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

            // Add request details
            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            // Save authentication
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            log.info("User authenticated successfully");

        } catch (Exception ex) {

            log.error("JWT Authentication Error", ex);

        }

        filterChain.doFilter(request, response);
    }
}