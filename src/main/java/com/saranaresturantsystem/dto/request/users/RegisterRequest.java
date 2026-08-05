package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName,
        @NotNull(message = "Username is required")
        String username,
        @NotNull(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,
        String phone,
        @NotNull(message = "Password is required")
        String password,
        @NotNull(message = "Confirm password is required")
        String confirmPassword
) {
}
