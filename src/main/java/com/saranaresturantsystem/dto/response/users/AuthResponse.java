package com.saranaresturantsystem.dto.response.users;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private String username;
    private String email;
    private List<String> roles;
    private List<String> permissions;
    private Long storeId;
}
