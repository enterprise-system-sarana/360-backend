package com.saranaresturantsystem.mappers.users;

import com.saranaresturantsystem.dto.request.users.PermissionRequest;
import com.saranaresturantsystem.dto.response.users.PermissionResponse;
import com.saranaresturantsystem.entities.users.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring" , uses = {PermissionGroupMapper.class})
public interface PermissionMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group.id", source = "groupId")
    @Mapping(target = "group.code", source = "groupCode")
    @Mapping(target = "group.name", source = "groupName")
    List<PermissionResponse> toListPermissionResponse(List<Permission> permissions);

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupCode", source = "group.code")
    @Mapping(target = "groupName", source = "group.name")
    @Mapping(target = "checked", ignore = true)
    PermissionResponse toResponse(Permission permission);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", ignore = true)
    Permission toEntity(PermissionRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", ignore = true)
    void updateFromRequest(PermissionRequest request, @MappingTarget Permission permission);
}
