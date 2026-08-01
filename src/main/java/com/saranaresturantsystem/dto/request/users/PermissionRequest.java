package com.saranaresturantsystem.dto.request.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PermissionRequest {
    @NotBlank(message = "Permission code is required")
    private String code;
    @NotBlank(message = "Permission name is required")
    private String name;
    private String description;
    @NotNull(message = "Permission group id is required")
    private Long groupId;
}
