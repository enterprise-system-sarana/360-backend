package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
public class LoginRequest {
    @NotBlank
    @NotNull(message = "Username is required")
    private String usernameOrEmail;

    @NotBlank
    @NotNull(message = "Password is required")
    private String password;
}

