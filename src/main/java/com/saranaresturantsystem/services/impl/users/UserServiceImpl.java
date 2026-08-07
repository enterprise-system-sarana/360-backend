package com.saranaresturantsystem.services.impl.users;

import com.saranaresturantsystem.constants.Constants;
import com.saranaresturantsystem.dto.request.users.UserRequest;
import com.saranaresturantsystem.dto.response.users.UserResponse;
import com.saranaresturantsystem.entities.users.Role;
import com.saranaresturantsystem.entities.users.User;
import com.saranaresturantsystem.execption.DuplicateResourceException;
import com.saranaresturantsystem.execption.ResourceNotFoundException;
import com.saranaresturantsystem.mappers.users.UserMapper;
import com.saranaresturantsystem.repository.users.RoleRepository;
import com.saranaresturantsystem.repository.users.UserRepository;
import com.saranaresturantsystem.services.interfaces.users.UserService;
import com.saranaresturantsystem.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(Map<String, String> params) {
        Pageable pageable = PageUtil.fromParams(params);
        Page<User> usersPage = userRepository.findByDeletedAtIsNull(pageable);
        return usersPage.map(userMapper::toResponse);
    }

    @Cacheable(value = "users", key = "#id")
    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = findById(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new DuplicateResourceException("Username already exists: " + request.username());
        }
        if (request.username() != null && userRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateResourceException("Email already exists: " + request.email());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        user.setIsActive(Constants.STATUS_ACTIVE);
        user.setIsVerified(true);
        user.setIsLocked(false);

        if (request.roleCodes() != null && !request.roleCodes().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String code : request.roleCodes()) {
                Role role = roleRepository.findByCode(code)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with code: " + code));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @CacheEvict(value = "users", key = "#id")
    @Override
    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = findById(id);

        if (request.username() != null && !request.username().equals(user.getUsername())) {
            if (userRepository.findByUsername(request.username()).isPresent()) {
                throw new DuplicateResourceException("Username already exists: " + request.username());
            }
            user.setUsername(request.username());
        }

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            if (userRepository.findByEmail(request.email()).isPresent()) {
                throw new DuplicateResourceException("Email already exists: " + request.email());
            }
            user.setEmail(request.email());
        }

        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        if (request.isActive() != null) {
            user.setIsActive(Constants.STATUS_ACTIVE);
        }

        if (request.roleCodes() != null) {
            Set<Role> roles = new HashSet<>();
            for (String code : request.roleCodes()) {
                Role role = roleRepository.findByCode(code)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with code: " + code));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @CacheEvict(value = "users", key = "#id")
    @Override
    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        user.setDeletedAt(LocalDateTime.now());
        user.setIsActive(Constants.STATUS_DELETE);
        userRepository.save(user);
    }

    @Cacheable(value = "users", key = "#id")
    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        if (user.getDeletedAt() != null) {
            throw new ResourceNotFoundException("User", id);
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new UsernameNotFoundException("No authentication found in security context");
        }
        String usernameOrEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));
    }
}
