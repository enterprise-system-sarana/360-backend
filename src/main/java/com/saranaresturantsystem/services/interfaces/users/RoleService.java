package com.saranaresturantsystem.services.interfaces.users;

import com.saranaresturantsystem.dto.request.users.RoleRequest;
import com.saranaresturantsystem.dto.response.users.PermissionResponse;
import com.saranaresturantsystem.dto.response.users.RoleResponse;

import java.util.List;
import java.util.Set;

public interface RoleService {
    List<RoleResponse> getAll();
    RoleResponse getById(Long id);
    RoleResponse create(RoleRequest request);
    RoleResponse update(Long id, RoleRequest request);
    void delete(Long id);
    List<PermissionResponse> getPermissionsByRoleId(Long roleId);
    List<PermissionResponse> updatePermissionsByRoleId(Long roleId, Set<Long> permissionIds);
}
