package com.saranaresturantsystem.services.impl.users;

import com.saranaresturantsystem.dto.request.users.RoleRequest;
import com.saranaresturantsystem.dto.response.users.PermissionResponse;
import com.saranaresturantsystem.dto.response.users.RoleResponse;
import com.saranaresturantsystem.entities.users.Permission;
import com.saranaresturantsystem.entities.users.Role;
import com.saranaresturantsystem.execption.DuplicateResourceException;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.users.PermissionMapper;
import com.saranaresturantsystem.mappers.users.RoleMapper;
import com.saranaresturantsystem.repository.users.PermissionRepository;
import com.saranaresturantsystem.repository.users.RoleRepository;
import com.saranaresturantsystem.services.interfaces.users.RoleService;
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
    private  final RoleMapper roleMapper ;
    private final PermissionMapper permissionMapper ;
    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRespoonse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getById(Long id) {
        Role role = roleRepository.findWithPermissionsById(id).orElseThrow(() -> new ResourceNotFoundException("Role", id));
        return roleMapper.toRespoonse(role);
    }

    @Override
    @Transactional
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.findByCode(request.code()).isPresent()) {
            throw new DuplicateResourceException("Role code already exists: " + request.code());
        }

         Role role = roleMapper.toEntity(request);
//        Role role = new Role();
//        role.setCode(request.code());
//        role.setName(request.name());
//        role.setDescription(request.description());

        // Assign Permissions
        if (request.permissionIds() != null && !request.permissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(request.permissionIds()));
            role.setPermissions(permissions);
        }

        Role saved = roleRepository.save(role);
        log.info("Created new Role [code={}] with {} permissions", saved.getCode(), saved.getPermissions() != null ? saved.getPermissions().size() : 0);
        return roleMapper.toRespoonse(saved);
    }

    @Override
    @Transactional
    public RoleResponse update(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));

        if (!role.getCode().equals(request.code())) {
            if (roleRepository.findByCode(request.code()).isPresent()) {
                throw new DuplicateResourceException("Role code already exists: " + request.code());
            }
            role.setCode(request.code());
        }

        role.setName(request.name());
        role.setDescription(request.description());

        // Update Permissions
        if (request.permissionIds() != null) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(request.permissionIds()));
            role.setPermissions(permissions);
        }

        Role saved = roleRepository.save(role);
        log.info("Updated Role [id={}, code={}] with {} permissions", saved.getId(), saved.getCode(), saved.getPermissions() != null ? saved.getPermissions().size() : 0);
        return roleMapper.toRespoonse(saved);
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
                    PermissionResponse response = permissionMapper.toResponse(permission);
                    return new PermissionResponse(
                            response.id(),
                            response.code(),
                            response.name(),
                            response.description(),
                            response.groupId(),
                            response.groupCode(),
                            response.groupName(),
                            assignedPermissionIds.contains(permission.getId())
                    );
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
        return saved.getPermissions().stream().map(roleMapper::toPermissionResponse).toList();
    }

}
