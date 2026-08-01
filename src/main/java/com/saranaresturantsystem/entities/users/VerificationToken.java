package com.saranaresturantsystem.entities.users;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.saranaresturantsystem.entities.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "tbl_verification_tokens", indexes = {
        @Index(name = "idx_verif_token", columnList = "token"),
        @Index(name = "idx_verif_user_type", columnList = "user_id, type")
})
public class VerificationToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "type", nullable = false)
    private String type; // e.g. "EMAIL_VERIFICATION", "PASSWORD_RESET"

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
