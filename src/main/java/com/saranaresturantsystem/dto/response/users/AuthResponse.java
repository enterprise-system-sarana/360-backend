package com.saranaresturantsystem.dto.response.users;


import lombok.Builder;

import java.util.List;
@Builder
public record AuthResponse
        (
                String accessToken,
                String refreshToken,
                String tokenType,
                Long expiresIn,
                Long userId,
                String username,
                String email,
                String firstName,
                String lastName,
                String profileImage,
                List<String> roles,
                List<String> permissions
        ) {}

