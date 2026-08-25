package com.saranaresturantsystem.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import com.saranaresturantsystem.audit.AuditListener;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditListener.class)
public abstract class BaseEntity {

    private static final ZoneId PHNOM_PENH = ZoneId.of("Asia/Phnom_Penh");

    @Column(name = "created_at", updatable = false, insertable = true)
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    @PrePersist
    public void onPrePersist() {
        this.createdAt = LocalDateTime.now(PHNOM_PENH);
        if (this.createdBy == null) {
            this.createdBy = getCurrentAuditor();
        }
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = LocalDateTime.now(PHNOM_PENH);
        this.updatedBy = getCurrentAuditor();
    }

    private String getCurrentAuditor() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                return auth.getName();
            }
        } catch (Exception ignored) {
        }
        return "SYSTEM";
    }
}
