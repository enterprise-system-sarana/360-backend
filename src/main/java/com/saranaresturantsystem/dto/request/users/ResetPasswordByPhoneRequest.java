package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordByPhoneRequest(
        @NotBlank(message = "Username or email is required")
        String usernameOrEmail,

        @NotBlank(message = "Phone number is required")
        String phone,

        @NotBlank(message = "New password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String newPassword
) {
}

