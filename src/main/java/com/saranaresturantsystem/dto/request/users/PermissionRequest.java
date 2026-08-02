package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;



public record PermissionRequest

        (
                @NotNull(message = "Permission code is required")
                String code,
                @NotNull(message = "Permission name is required")
                String name,
                String description
                , long groupId
        ) {
}
