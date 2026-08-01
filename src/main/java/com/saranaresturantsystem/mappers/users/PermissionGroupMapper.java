package com.saranaresturantsystem.mappers.users;

import com.saranaresturantsystem.dto.request.users.PermissionGroupRequest;
import com.saranaresturantsystem.dto.response.users.PermissionGroupResponse;
import com.saranaresturantsystem.entities.users.PermissionGroup;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionGroupMapper {
    List<PermissionGroupResponse> toListPermissionGroupResponse(List<PermissionGroup> permissionGroups);
    PermissionGroupResponse toResponse(PermissionGroup permissionGroup);

    PermissionGroup toEntity(PermissionGroupRequest request);

    void updateFromRequest(PermissionGroupRequest request, @MappingTarget PermissionGroup group);
}
