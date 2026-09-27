package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public record UserRequest(
        String firstName,
        String lastName,
        String phone ,
        @NotBlank
        @Email
        String email,
        String password,
        String profileImage,
        String isActive,
        Set<Long> roleIds
) {
}
