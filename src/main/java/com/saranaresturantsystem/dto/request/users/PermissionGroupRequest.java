package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.NotNull;


public record PermissionGroupRequest
        (
                @NotNull(message = "Permission group code is required")
                String code,
                @NotNull(message = "Permission group name is required")
                String name,
                String description
        ) {

}
