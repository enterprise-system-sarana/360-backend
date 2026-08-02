package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public record LoginRequest(
        @NotBlank
        @NotNull(message = "Username  is required")
        String usernameOrEmail,
        @NotBlank
        @NotNull(message = "Password is required")
        String password
) {

}
