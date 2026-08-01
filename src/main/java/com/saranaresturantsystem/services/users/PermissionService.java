package com.saranaresturantsystem.services.users;

import com.saranaresturantsystem.dto.request.users.PermissionRequest;
import com.saranaresturantsystem.dto.response.users.PermissionResponse;
import com.saranaresturantsystem.entities.users.Permission;

import java.util.List;

public interface PermissionService {
    List<PermissionResponse> getAll();
    PermissionResponse getById(Long id);
    PermissionResponse create(PermissionRequest request);
    PermissionResponse update(Long id, PermissionRequest request);
    void delete(Long id);
    Permission findById(Long id);
}
