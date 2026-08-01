package com.saranaresturantsystem.services.users;

import com.saranaresturantsystem.dto.request.users.PermissionGroupRequest;
import com.saranaresturantsystem.dto.response.users.PermissionGroupResponse;
import com.saranaresturantsystem.entities.users.PermissionGroup;

import java.util.List;

public interface PermissionGroupService {
    List<PermissionGroupResponse> getAll();
    PermissionGroupResponse getById(Long id);
    PermissionGroupResponse create(PermissionGroupRequest request);
    PermissionGroupResponse update(Long id, PermissionGroupRequest request);
    void delete(Long id);
    PermissionGroup findById(Long id);
}
