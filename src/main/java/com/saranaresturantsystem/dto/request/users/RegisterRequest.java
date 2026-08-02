package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotNull(message = "Username is required")
        String username,
        @NotNull(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,
        @NotNull(message = "Password is required")
        String password
) {
}
