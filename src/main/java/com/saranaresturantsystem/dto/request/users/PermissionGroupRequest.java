package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionGroupRequest {
    @NotBlank(message = "Permission group code is required")
    private String code;
    @NotBlank(message = "Permission group name is required")
    private String name;
    private String description;
}
