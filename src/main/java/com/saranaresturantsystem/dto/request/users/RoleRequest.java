package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.NotBlank;


import java.util.Set;


public record RoleRequest
        (@NotBlank(message = "Role code is required")
         String code,
         @NotBlank(message = "Role name is required")
         String name,
         String description,
         Set<Long> permissionIds) {

}
