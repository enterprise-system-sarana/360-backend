package com.saranaresturantsystem.dto.response.users;


import java.time.LocalDateTime;
import java.util.List;

public record UserResponse(
        Long id ,
        String lastName ,
        String firstName ,
        String phone ,
        String username ,
        String email ,
        String profileImage,
        String isActive ,
        String isVerified,
        String isLocked,
        List<String> roles ,
        LocalDateTime createdAt ,
        LocalDateTime updatedAt
) {

}
