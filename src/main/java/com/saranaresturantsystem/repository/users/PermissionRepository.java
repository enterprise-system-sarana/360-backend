package com.saranaresturantsystem.repository.users;

import com.saranaresturantsystem.entities.users.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByCode(String code);
    
}
