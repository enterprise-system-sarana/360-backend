package com.saranaresturantsystem.services.impl.users;

import com.saranaresturantsystem.dto.request.users.PermissionGroupRequest;
import com.saranaresturantsystem.dto.response.users.PermissionGroupResponse;
import com.saranaresturantsystem.entities.users.PermissionGroup;
import com.saranaresturantsystem.execption.DuplicateResourceException;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.users.PermissionGroupMapper;
import com.saranaresturantsystem.repository.users.PermissionGroupRepository;
import com.saranaresturantsystem.services.interfaces.users.PermissionGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionGroupServiceImpl implements PermissionGroupService {

    private final PermissionGroupRepository permissionGroupRepository;
    private final PermissionGroupMapper permissionGroupMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PermissionGroupResponse> getAll() {
        List<PermissionGroup> groups = permissionGroupRepository.findAll();
        return permissionGroupMapper.toListPermissionGroupResponse(groups);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionGroupResponse getById(Long id) {
        PermissionGroup group = findById(id);
        return permissionGroupMapper.toResponse(group);
    }

    @Override
    @Transactional
    public PermissionGroupResponse create(PermissionGroupRequest request) {
        if (permissionGroupRepository.findByCode(request.code()).isPresent()) {
            throw new DuplicateResourceException("Permission group code already exists: " + request.code());
        }
        PermissionGroup group = permissionGroupMapper.toEntity(request);
        PermissionGroup saved = permissionGroupRepository.save(group);
        log.info("Created new PermissionGroup [code={}]", saved.getCode());
        return permissionGroupMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PermissionGroupResponse update(Long id, PermissionGroupRequest request) {
        PermissionGroup group = findById(id);
        if (!group.getCode().equals(request.code())) {
            if (permissionGroupRepository.findByCode(request.code()).isPresent()) {
                throw new DuplicateResourceException("Permission group code already exists: " + request.code());
            }
        }
        permissionGroupMapper.updateFromRequest(request, group);
        PermissionGroup saved = permissionGroupRepository.save(group);
        log.info("Updated PermissionGroup [id={}, code={}]", saved.getId(), saved.getCode());
        return permissionGroupMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        PermissionGroup group = findById(id);
        permissionGroupRepository.delete(group);
        log.info("Deleted PermissionGroup [id={}]", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionGroup findById(Long id) {
        return permissionGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PermissionGroup", id));
    }
}
