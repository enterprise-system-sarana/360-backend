package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "Email or username is required")
        String emailOrUsername
) {
}
