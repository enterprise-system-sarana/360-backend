package com.saranaresturantsystem.entities.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tbl_permission_groups", indexes = {
        @Index(name = "idx_perm_group_code", columnList = "code")
})
public class PermissionGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    private Set<Permission> permissions = new HashSet<>();
}
