package com.saranaresturantsystem.dto.response.users;


public record PermissionResponse
        (
                Long id,
                String code,
                String name,
                String description,
                String groupId,
                String groupCode,
                String groupName,
                Boolean checked
        ) {

}
