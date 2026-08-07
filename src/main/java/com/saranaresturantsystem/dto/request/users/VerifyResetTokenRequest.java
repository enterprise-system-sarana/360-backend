package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.NotBlank;

public record VerifyResetTokenRequest(
        @NotBlank(message = "Token is required")
        String token
) {
}
