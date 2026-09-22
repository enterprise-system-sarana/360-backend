package com.saranaresturantsystem.config.security;

import io.jsonwebtoken.Claims;
import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.entities.users.Permission;
import com.saranaresturantsystem.entities.users.Role;
import com.saranaresturantsystem.entities.users.User;
import com.saranaresturantsystem.repository.users.UserRepository;
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
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authHeader.substring(7);
            if (!jwtService.isAccessTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            Claims claims = jwtService.extractAllClaims(token);
            Long userId = claims.get("uid", Long.class);
            if (userId == null) {
                filterChain.doFilter(request, response);
                return;
            }

            User user = userRepository.findWithRolesAndPermissionsById(userId).orElse(null);
            if (user == null || !Constants.STATUS_ACTIVE.equalsIgnoreCase(user.getIsActive())
                    || Boolean.TRUE.equals(user.getIsLocked()) || user.getDeletedAt() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            for (Role role : user.getRoles()) {
                String roleCode = role.getCode();
                if (roleCode != null) {
                    authorities.add(new SimpleGrantedAuthority(roleCode.startsWith("ROLE_") ? roleCode : "ROLE_" + roleCode));
                }
                for (Permission permission : role.getPermissions()) {
                    if (permission.getCode() != null) {
                        authorities.add(new SimpleGrantedAuthority(permission.getCode()));
                    }
                }
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ex) {
            log.debug("JWT authentication rejected: {}", ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}