package com.saranaresturantsystem.dto.response.users;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String isActive;
    private Boolean isVerified;
    private Boolean isLocked;
    private Long storeId;
    private String storeName;
    private List<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
