//package com.saranaresturantsystem.services.impl.users;
//
//import com.saranaresturantsystem.dto.request.users.UserRequest;
//import com.saranaresturantsystem.dto.response.users.UserResponse;
//import com.saranaresturantsystem.entities.inventory.Store;
//import com.saranaresturantsystem.entities.users.Role;
//import com.saranaresturantsystem.entities.users.User;
//import com.saranaresturantsystem.enums.StatusType;
//import com.saranaresturantsystem.execption.DuplicateResourceException;
//import com.saranaresturantsystem.execption.ResourceNotFoundException;
//import com.saranaresturantsystem.mappers.users.UserMapper;
//import com.saranaresturantsystem.repository.users.RoleRepository;
//import com.saranaresturantsystem.repository.users.UserRepository;
//import com.saranaresturantsystem.services.users.UserService;
//import com.saranaresturantsystem.utils.PageUtil;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//
//import java.time.LocalDateTime;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Set;
//
//@Service
//@RequiredArgsConstructor
//public class UserServiceImpl implements UserService {
//
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//    private final PasswordEncoder passwordEncoder;
//    private  final UserMapper userMapper ;
//    private final StoreService storeService;
//
//    @Override
//    public Page<UserResponse> getAll(Map<String, String> params) {
//        User currentUser = getCurrentUser();
//        Pageable pageable = PageUtil.fromParams(params);
//        Page<User> usersPage;
//        if (currentUser.getStore() != null) {
//            boolean isSuperAdmin = hasRole(currentUser, "SUPER_ADMIN");
//            if (!isSuperAdmin) {
//                usersPage = userRepository.findByStoreIdAndDeletedAtIsNull(currentUser.getStore().getId(), pageable);
//            } else {
//                usersPage = userRepository.findByDeletedAtIsNull(pageable);
//            }
//        } else {
//            usersPage = userRepository.findByDeletedAtIsNull(pageable);
//        }
//        return usersPage.map(userMapper::toResponse);
//    }
//
//    @Override
//    public UserResponse getById(Long id) {
//        User currentUser = getCurrentUser();
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("User", id));
//
//        if (user.getDeletedAt() != null) {
//            throw new ResourceNotFoundException("User", id);
//        }
//
//        if (currentUser.getStore() != null) {
//            boolean isSuperAdmin = hasRole(currentUser, "SUPER_ADMIN");
//            if (!isSuperAdmin) {
//                if (user.getStore() == null || !user.getStore().getId().equals(currentUser.getStore().getId())) {
//                    throw new AccessDeniedException("You do not have permission to access this user.");
//                }
//            }
//        }
//        return userMapper.toResponse(user);
//    }
//
//    @Override
//    @Transactional
//    public UserResponse create(UserRequest request) {
//        User currentUser = getCurrentUser();
//        Long targetStoreId = request.getStoreId();
//
//        // Enforce store boundary for non-super-admins
//        if (currentUser.getStore() != null) {
//            boolean isSuperAdmin = hasRole(currentUser, "SUPER_ADMIN");
//            if (!isSuperAdmin) {
//                if (targetStoreId == null || !targetStoreId.equals(currentUser.getStore().getId())) {
//                    throw new AccessDeniedException("You can only create users for your own store.");
//                }
//            }
//        }
//
//        // Validate unique username & email
//        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
//        throw  new  DuplicateResourceException("Username already exists");
//        }
//        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
//            throw new DuplicateResourceException("Email already exists");
//        }
//        User user = userMapper.toEntity(request);
//        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
//        user.setIsLocked(false);
//        user.setIsVerified(true);
//        if (targetStoreId != null) {
//            Store store = storeService.findById(targetStoreId);
//            user.setStore(store);
//        }
//
//        // Handle Role Assignments
//        Set<Role> roles = new HashSet<>();
//        if (request.getRoleCodes() != null && !request.getRoleCodes().isEmpty()) {
//            for (String code : request.getRoleCodes()) {
//                Role role = roleRepository.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("Role not found with code: " + code));
//                // Restrict store admin role assignments
//                if (currentUser.getStore() != null && !hasRole(currentUser, "SUPER_ADMIN")) {
//                    if (!"ROLE_STAFF".equals(code) && !"ROLE_SALE".equals(code) && !"ROLE_MANAGER".equals(code)) {
//                        throw new AccessDeniedException("Store Administrator can only assign ROLE_STAFF, ROLE_SALE, and ROLE_MANAGER roles.");}
//                }
//                roles.add(role);
//            }
//        }
//        user.setRoles(roles);
//        user = userRepository.save(user);
//        return userMapper.toResponse(user);
//    }
//
//
//    @Override
//    @Transactional
//    public UserResponse update(Long id, UserRequest request) {
//        User currentUser = getCurrentUser();
//        User user = findById(id);
//
//        // Store boundary checks
//        if (currentUser.getStore() != null) {
//            boolean isSuperAdmin = hasRole(currentUser, "SUPER_ADMIN");
//            if (!isSuperAdmin) {
//                if (user.getStore() == null || !user.getStore().getId().equals(currentUser.getStore().getId())) {
//                    throw new AccessDeniedException("You do not have permission to manage this user.");
//                }
//                if (request.getStoreId() == null || !request.getStoreId().equals(currentUser.getStore().getId())) {
//                    throw new AccessDeniedException("You cannot transfer users to other stores.");
//                }
//            }
//        }
//
//        if (request.getPassword() != null && !request.getPassword().isBlank()) {
//            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
//        }
//        user.setIsActive(request.getIsActive() );
//        if (request.getRoleCodes() != null) {
//            Set<Role> roles = new java.util.HashSet<>();
//            for (String code : request.getRoleCodes()) {
//                Role role = roleRepository.findByCode(code)
//                        .orElseThrow(() -> new ResourceNotFoundException("Role not found with code: " + code));
//
//                if (currentUser.getStore() != null && !hasRole(currentUser, "SUPER_ADMIN")) {
//                    if (!"STAFF".equals(code) && !"SALE".equals(code) && !"ADMIN".equals(code)) {
//                        throw new AccessDeniedException(
//                                "Store Administrator can only assign ROLE_STAFF, ROLE_SALE, and ROLE_MANAGER roles."
//                        );
//                    }
//                }
//                roles.add(role);
//            }
//            user.setRoles(roles);
//        }
//
//        user = userRepository.save(user);
//        return userMapper.toResponse(user);
//    }
//
//    @Override
//    @Transactional
//    public void delete(Long id) {
//        User currentUser = getCurrentUser();
//        User user = findById(id);
//        if (currentUser.getStore() != null) {
//            boolean isSuperAdmin = hasRole(currentUser, "SUPER_ADMIN");
//            if (!isSuperAdmin) {
//                if (user.getStore() == null || !user.getStore().getId().equals(currentUser.getStore().getId())) {
//                    throw new AccessDeniedException("You do not have permission to delete this user.");
//                }
//            }
//        }
//        user.setDeletedAt(LocalDateTime.now());
//        user.setIsActive(StatusType.INACTIVE);
//        userRepository.save(user);
//    }
//
//    @Override
//    public User findById(Long id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("User", id));
//        if (user.getDeletedAt() != null) {
//            throw new ResourceNotFoundException("User", id);
//        }
//        return  user;
//    }
//
//    public User getCurrentUser() {
//        String usernameOrEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
//        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
//                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found"));
//    }
//
//    private boolean hasRole(User user, String roleCode) {
//        return user.getRoles().stream().anyMatch(r -> roleCode.equals(r.getCode()));
//    }
//
//}
