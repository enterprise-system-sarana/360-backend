package com.saranaresturantsystem.entities.users;

import com.saranaresturantsystem.entities.BaseEntity;
import com.saranaresturantsystem.enums.StatusType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tbl_users", indexes = {@Index(name = "idx_users_active", columnList = "is_active, is_locked, deleted_at")})
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;
    @Column(name = "email", length = 150, unique = true, nullable = false)
    private String email;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Column(name = "is_active" , length = 50)
    private String isActive;
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    @Column(name = "is_locked")
    private Boolean isLocked = false;
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tbl_user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_user_role", columnNames = {"user_id", "role_id"})
    )
    private Set<Role> roles ;

}
