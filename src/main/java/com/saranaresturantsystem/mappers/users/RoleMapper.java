package com.saranaresturantsystem.mappers.users;

import com.saranaresturantsystem.dto.request.users.RoleRequest;
import com.saranaresturantsystem.dto.response.users.PermissionResponse;
import com.saranaresturantsystem.dto.response.users.RoleResponse;
import com.saranaresturantsystem.entities.users.Permission;
import com.saranaresturantsystem.entities.users.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissionIds", source = "permissions")
    RoleResponse toResponse(Role role);

    default Set<Long> map(Set<Permission> permissions) {
        if (permissions == null) {
            return Collections.emptySet();
        }

        return permissions.stream()
                .map(Permission::getId)
                .collect(Collectors.toSet());
    }

    Role toEntity(RoleRequest request);


    PermissionResponse toPermissionResponse (Permission permission);

    void updateEntityFromRequest(RoleRequest request , @MappingTarget Role role);
}
