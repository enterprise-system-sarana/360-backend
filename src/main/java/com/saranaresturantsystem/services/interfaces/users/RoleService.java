package com.saranaresturantsystem.services.interfaces.users;

import com.saranaresturantsystem.dto.request.users.RoleRequest;
import com.saranaresturantsystem.dto.response.users.PermissionResponse;
import com.saranaresturantsystem.dto.response.users.RoleResponse;
import com.saranaresturantsystem.entities.users.Role;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoleService {
    Page<RoleResponse> getAllRole(Map<String , String> params);
    List<RoleResponse> getAll();
    Role findById(Long id);
    RoleResponse getById(Long id);
    RoleResponse create(RoleRequest request);
    RoleResponse update(Long id, RoleRequest request);
    void delete(Long id);
    List<PermissionResponse> getPermissionsByRoleId(Long roleId);
    List<PermissionResponse> updatePermissionsByRoleId(Long roleId, Set<Long> permissionIds);
}
