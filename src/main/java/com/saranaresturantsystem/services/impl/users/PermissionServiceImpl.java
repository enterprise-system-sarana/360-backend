package com.saranaresturantsystem.services.impl.users;

import com.saranaresturantsystem.dto.request.users.PermissionRequest;
import com.saranaresturantsystem.dto.response.users.PermissionResponse;
import com.saranaresturantsystem.entities.users.Permission;
import com.saranaresturantsystem.entities.users.PermissionGroup;
import com.saranaresturantsystem.execption.DuplicateResourceException;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.users.PermissionMapper;
import com.saranaresturantsystem.repository.users.PermissionGroupRepository;
import com.saranaresturantsystem.repository.users.PermissionRepository;
import com.saranaresturantsystem.services.users.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionGroupRepository permissionGroupRepository;
    private final PermissionMapper permissionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> getAll() {
        return permissionMapper.toListPermissionResponse(permissionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse getById(Long id) {
        Permission permission = findById(id);
        return permissionMapper.toResponse(permission);
    }

    @Override
    @Transactional
    public PermissionResponse create(PermissionRequest request) {
        if (permissionRepository.findByCode(request.getCode()).isPresent()) {
            throw new DuplicateResourceException("Permission code already exists: " + request.getCode());
        }

        PermissionGroup group = permissionGroupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("PermissionGroup", request.getGroupId()));

        Permission permission = permissionMapper.toEntity(request);
        permission.setGroup(group);

        Permission saved = permissionRepository.save(permission);
        log.info("Created new Permission [code={}]", saved.getCode());
        return permissionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PermissionResponse update(Long id, PermissionRequest request) {
        Permission permission = findById(id);
        if (!permission.getCode().equals(request.getCode())) {
            if (permissionRepository.findByCode(request.getCode()).isPresent()) {
                throw new DuplicateResourceException("Permission code already exists: " + request.getCode());
            }
        }

        PermissionGroup group = permissionGroupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("PermissionGroup", request.getGroupId()));

        permissionMapper.updateFromRequest(request, permission);
        permission.setGroup(group);

        Permission saved = permissionRepository.save(permission);
        log.info("Updated Permission [id={}, code={}]", saved.getId(), saved.getCode());
        return permissionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Permission permission = findById(id);
        permissionRepository.delete(permission);
        log.info("Deleted Permission [id={}]", id);
    }

    @Override
    public Permission findById(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission", id));
    }
}
