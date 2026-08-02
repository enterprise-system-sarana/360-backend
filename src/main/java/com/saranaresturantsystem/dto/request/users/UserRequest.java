package com.saranaresturantsystem.dto.request.users;

import com.saranaresturantsystem.enums.StatusType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record UserRequest(
        @NotBlank
        String username,
        @NotBlank
        @Email
        String email,
        String password,
        StatusType isActive,
        Set<String> roleCodes
) {
}
