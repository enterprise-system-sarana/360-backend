package com.saranaresturantsystem.dto.response.users;


import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id ,
        String username ,
        String email ,
        String isActive ,
        String isVerified,
        String isLocked,
        List<String> roles ,
        LocalDateTime createdAt ,
        LocalDateTime updatedAt
) {

}
