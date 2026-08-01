package com.saranaresturantsystem.services.impl.users;

import com.saranaresturantsystem.dto.request.users.RoleRequest;
import com.saranaresturantsystem.dto.response.users.PermissionResponse;
import com.saranaresturantsystem.dto.response.users.RoleResponse;
import com.saranaresturantsystem.entities.users.Permission;
import com.saranaresturantsystem.entities.users.Role;
import com.saranaresturantsystem.execption.DuplicateResourceException;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.repository.users.PermissionRepository;
import com.saranaresturantsystem.repository.users.RoleRepository;
import com.saranaresturantsystem.services.users.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getById(Long id) {
        Role role = roleRepository.findWithPermissionsById(id).orElseThrow(() -> new ResourceNotFoundException("Role", id));
        return toResponse(role);
    }

    @Override
    @Transactional
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.findByCode(request.getCode()).isPresent()) {
            throw new DuplicateResourceException("Role code already exists: " + request.getCode());
        }

        Role role = new Role();
        role.setCode(request.getCode());
        role.setName(request.getName());
        role.setDescription(request.getDescription());

        // Assign Permissions
        if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(request.getPermissionIds()));
            role.setPermissions(permissions);
        }

        Role saved = roleRepository.save(role);
        log.info("Created new Role [code={}] with {} permissions", saved.getCode(), saved.getPermissions() != null ? saved.getPermissions().size() : 0);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public RoleResponse update(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));

        if (!role.getCode().equals(request.getCode())) {
            if (roleRepository.findByCode(request.getCode()).isPresent()) {
                throw new DuplicateResourceException("Role code already exists: " + request.getCode());
            }
            role.setCode(request.getCode());
        }

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        // Update Permissions
        if (request.getPermissionIds() != null) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(request.getPermissionIds()));
            role.setPermissions(permissions);
        }

        Role saved = roleRepository.save(role);
        log.info("Updated Role [id={}, code={}] with {} permissions", saved.getId(), saved.getCode(), saved.getPermissions() != null ? saved.getPermissions().size() : 0);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        
        roleRepository.delete(role);
        log.info("Deleted Role [id={}]", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getPermissionsByRoleId(Long roleId) {
        Role role = roleRepository.findWithPermissionsById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", roleId));

        Set<Long> assignedPermissionIds = role.getPermissions().stream()
                .map(Permission::getId)
                .collect(Collectors.toSet());

        List<Permission> allPermissions = permissionRepository.findAll();
        return allPermissions.stream()
                .map(permission -> {
                    PermissionResponse response = toPermissionResponse(permission);
                    response.setChecked(assignedPermissionIds.contains(permission.getId()));
                    return response;
                })
                .toList();
    }

    @Override
    @Transactional
    public List<PermissionResponse> updatePermissionsByRoleId(Long roleId, Set<Long> permissionIds) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role", roleId));
        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        role.setPermissions(permissions);
        Role saved = roleRepository.save(role);
        log.info("Updated permissions for Role [id={}, code={}] with {} permissions", saved.getId(), saved.getCode(), saved.getPermissions().size());
        return saved.getPermissions().stream().map(this::toPermissionResponse).toList();
    }

    private RoleResponse toResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setCode(role.getCode());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        if (role.getPermissions() != null) {
            response.setPermissionIds(role.getPermissions().stream().map(Permission::getId).collect(Collectors.toSet()));
        } else {
            response.setPermissionIds(new HashSet<>());
        }
        return response;
    }

    private PermissionResponse toPermissionResponse(Permission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setId(permission.getId());
        response.setCode(permission.getCode());
        response.setName(permission.getName());
        response.setDescription(permission.getDescription());
        if (permission.getGroup() != null) {
            response.setGroupId(permission.getGroup().getId());
            response.setGroupCode(permission.getGroup().getCode());
            response.setGroupName(permission.getGroup().getName());
        }
        return response;
    }
}
