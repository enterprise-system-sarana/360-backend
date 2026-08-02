package com.saranaresturantsystem.dto.request.users;


import jakarta.validation.constraints.NotBlank;

public record LogoutRequest (
        @NotBlank
        String refreshToken) {
}
