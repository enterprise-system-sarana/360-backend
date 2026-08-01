package com.saranaresturantsystem.dto.request.users;

import com.saranaresturantsystem.enums.StatusType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.Set;

@Data
public class UserRequest {
    @NotBlank
    private String username;
    @NotBlank
    @Email
    private String email;
    private String password;
    private StatusType isActive = StatusType.ACTIVE;
    private Long storeId;
    private Set<String> roleCodes;
}
