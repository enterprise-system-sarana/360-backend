package com.saranaresturantsystem.services.impl.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saranaresturantsystem.common.UniqueChecker;
import com.saranaresturantsystem.constants.Constants;
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
import com.saranaresturantsystem.specification.users.role.RoleFilter;
import com.saranaresturantsystem.specification.users.role.RoleSpec;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private  final UniqueChecker uniqueChecker ;
    private  final ObjectMapper objectMapper ;

    @Override
    public Page<RoleResponse> getAllRole(Map<String, String> params) {
        RoleFilter roleFilter = objectMapper.convertValue(params , RoleFilter.class);
        Pageable pageable = PageUtil.fromParams(params);
        Specification<Role> spec = RoleSpec.filter(roleFilter);
        return  roleRepository.findAll(spec,pageable).map(roleMapper::toResponse);
//        return null;
    }

    //    @Cacheable(value = "roles_list")
    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toResponse).toList();
    }

    @Override
    public Role findById(Long id) {
        Role findId = roleRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Role" , id));
      if (findId.getStatus().equals(Constants.STATUS_INIT) || findId.getStatus().equals(Constants.STATUS_DELETE)){
          throw new ResourceNotFoundException("Role : " + id);
      }
      return findId;
    };

    //    @Cacheable(value = "roles", key = "#id")
    @Override
    @Transactional(readOnly = true)
    public RoleResponse getById(Long id) {
        Role role = findById(id);
        return roleMapper.toResponse(role);
    }

//    @CacheEvict(value = "roles_list", allEntries = true)
    @Override
    @Transactional
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.findByCode(request.code()).isPresent()) {
            throw new DuplicateResourceException("Role " + request.code());
        }

         Role role = roleMapper.toEntity(request);
        role.setCode("ROLE_"+request.code());
        role.setStatus(Constants.STATUS_ACTIVE);
        if (request.permissionIds() != null && !request.permissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(request.permissionIds()));
            role.setPermissions(permissions);
        }

        Role saved = roleRepository.save(role);
        return roleMapper.toResponse(saved);
    }


    @Override
    @Transactional
    public RoleResponse update(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));

        uniqueChecker.verify(roleRepository,role , "code",request.code());
        role.setCode("ROLE_"+ request.code());
        role.setName(request.name());
        role.setDescription(request.description());
        // Update Permissions
        if (request.permissionIds() != null) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(request.permissionIds()));
            role.setPermissions(permissions);
        }
        Role saved = roleRepository.save(role);
        return roleMapper.toResponse(saved);
    }

//    @Caching(evict = {
//        @CacheEvict(value = "roles", key = "#id"),
//        @CacheEvict(value = "roles_list", allEntries = true)
//    })
    @Override
    @Transactional
    public void delete(Long id) {
//        Role role = roleRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
//
        Role role = findById(id);
        role.setStatus(Constants.STATUS_DELETE);
        roleRepository.save(role);
        roleRepository.delete(role);
    }

//    @Cacheable(value = "role_permissions", key = "#roleId")
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

//    @Caching(evict = {
//        @CacheEvict(value = "role_permissions", key = "#roleId"),
//        @CacheEvict(value = "roles", key = "#roleId"),
//        @CacheEvict(value = "roles_list", allEntries = true)
//    })
    @Override
    @Transactional
    public List<PermissionResponse> updatePermissionsByRoleId(Long roleId, Set<Long> permissionIds) {
        Role role = findById(roleId);
        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        role.setPermissions(permissions);
        Role saved = roleRepository.save(role);
        log.info("Updated permissions for Role [id={}, code={}] with {} permissions", saved.getId(), saved.getCode(), saved.getPermissions().size());
        return saved.getPermissions().stream().map(roleMapper::toPermissionResponse).toList();
    }

}
