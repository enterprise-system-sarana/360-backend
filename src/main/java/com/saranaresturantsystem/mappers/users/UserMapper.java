package com.saranaresturantsystem.mappers.users;

import com.saranaresturantsystem.dto.request.users.UserRequest;
import com.saranaresturantsystem.dto.response.users.UserResponse;
import com.saranaresturantsystem.entities.users.Role;
import com.saranaresturantsystem.entities.users.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles")
    UserResponse toResponse(User user);
    User toEntity(UserRequest user);
    void updateFromRequest(UserRequest request , @MappingTarget User user);

    default List<String> rolesToStrings(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .toList();
    }
}
