package com.saranaresturantsystem.controllers.users;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.saranaresturantsystem.common.Message;
import com.saranaresturantsystem.common.ResponseFactory;
import com.saranaresturantsystem.dto.request.users.PermissionGroupRequest;
import com.saranaresturantsystem.dto.response.users.PermissionGroupResponse;
import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.services.users.PermissionGroupService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/permission-group")
@Tag(name = "Permission Group Controller", description = "APIs for managing permission groups")
public class PermissionGroupController {
    private final PermissionGroupService permissionGroupService;

    @GetMapping
    @PreAuthorize("hasAuthority('permissionGroup:read')")
    public ResponseEntity<ApiResponse<List<PermissionGroupResponse>>> getAll() {
        List<PermissionGroupResponse> payload = permissionGroupService.getAll();
        return ResponseFactory.ok(payload, Message.getAll("PermissionGroup"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permissionGroup:read')")
    public ResponseEntity<ApiResponse<PermissionGroupResponse>> getById(@PathVariable Long id) {
        PermissionGroupResponse payload = permissionGroupService.getById(id);
        return ResponseFactory.ok(payload, Message.getById("PermissionGroup", id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permissionGroup:create')")
    public ResponseEntity<ApiResponse<PermissionGroupResponse>> create(@Valid @RequestBody PermissionGroupRequest request) {
        PermissionGroupResponse payload = permissionGroupService.create(request);
        return ResponseFactory.created(payload, "PermissionGroup");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permissionGroup:update')")
    public ResponseEntity<ApiResponse<PermissionGroupResponse>> update(@PathVariable Long id, @Valid @RequestBody PermissionGroupRequest request) {
        PermissionGroupResponse payload = permissionGroupService.update(id, request);
        return ResponseFactory.ok(payload, Message.updated("PermissionGroup", id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permissionGroup:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        permissionGroupService.delete(id);
        return ResponseFactory.deleted("PermissionGroup", id);
    }
}
