package com.saranaresturantsystem.dto.response.users;


import java.util.List;
import java.util.Set;
public record RoleResponse(
        Long id ,
        String code ,
        String name ,
        String description ,
        Set<Long> permissionIds ,
        List<PermissionResponse> permissions
) {

}
