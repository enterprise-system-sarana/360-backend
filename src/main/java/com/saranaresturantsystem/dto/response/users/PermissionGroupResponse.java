package com.saranaresturantsystem.dto.response.users;



public record PermissionGroupResponse(
        Long id ,
        String code ,
        String name ,
        String description
) {
}
