package com.saranaresturantsystem.repository.users;

import com.saranaresturantsystem.entities.users.Permission;
import com.saranaresturantsystem.entities.users.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> , JpaSpecificationExecutor<Role> {
    Optional<Role> findByCode(String code);

    @Query("SELECT p FROM Role r JOIN r.permissions p WHERE r.id = :roleId")
    Set<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.id = :id")
    Optional<Role> findWithPermissionsById(@Param("id") Long id);
}
