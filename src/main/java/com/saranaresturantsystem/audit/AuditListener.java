package com.saranaresturantsystem.audit;

import com.saranaresturantsystem.entities.users.AuditLog;
import com.saranaresturantsystem.repository.users.AuditLogRepository;
import jakarta.persistence.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import com.saranaresturantsystem.config.security.SpringContextHolder;


public class AuditListener {

    @PostPersist
    public void onPostPersist(Object entity) {
        logAction(entity, "INSERT");
    }

    @PostUpdate
    public void onPostUpdate(Object entity) {
        logAction(entity, "UPDATE");
    }

    @PostRemove
    public void onPostRemove(Object entity) {
        logAction(entity, "DELETE");
    }

    private void logAction(Object entity, String action) {
        // Prevent infinite recursion by not auditing AuditLog itself
        if (entity instanceof AuditLog) {
            return;
        }

        try {
            AuditLogRepository repository = SpringContextHolder.getBean(AuditLogRepository.class);
            if (repository == null) {
                return;
            }

            String tableName = getTableName(entity);
            String recordId = getEntityId(entity);
            String changedBy = getCurrentUser();
            String changes = getEntityDetails(entity);

            AuditLog auditLog = AuditLog.builder()
                    .tableName(tableName)
                    .action(action)
                    .recordId(recordId)
                    .changedBy(changedBy)
                    .changedAt(LocalDateTime.now())
                    .changes(changes)
                    .build();

            repository.save(auditLog);
        } catch (Exception e) {
            // Keep logging completely non-intrusive to the primary transaction
            System.err.println("Error saving audit log: " + e.getMessage());
        }
    }

    private String getTableName(Object entity) {
        Table tableAnnotation = entity.getClass().getAnnotation(Table.class);
        if (tableAnnotation != null && !tableAnnotation.name().isEmpty()) {
            return tableAnnotation.name();
        }
        return entity.getClass().getSimpleName();
    }

    private String getEntityId(Object entity) {
        try {
            Class<?> current = entity.getClass();
            while (current != null) {
                for (java.lang.reflect.Field field : current.getDeclaredFields()) {
                    if (field.isAnnotationPresent(Id.class)) {
                        field.setAccessible(true);
                        Object id = field.get(entity);
                        return id != null ? id.toString() : null;
                    }
                }
                current = current.getSuperclass();
            }
        } catch (Exception e) {
            // Fallback
        }
        return "UNKNOWN";
    }

    private String getEntityDetails(Object entity) {
        try {
            Map<String, String> map = new LinkedHashMap<>();
            Class<?> current = entity.getClass();
            while (current != null) {
                for (java.lang.reflect.Field field : current.getDeclaredFields()) {
                    // Skip compiler-generated, static, and relationship collections
                    if (field.isSynthetic() || java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    if (java.util.Collection.class.isAssignableFrom(field.getType()) || Map.class.isAssignableFrom(field.getType())) {
                        continue;
                    }

                    field.setAccessible(true);
                    try {
                        Object value = field.get(entity);
                        if (value != null) {
                            if (value.getClass().isAnnotationPresent(Entity.class)) {
                                map.put(field.getName(), "Entity(" + getEntityId(value) + ")");
                            } else {
                                map.put(field.getName(), value.toString());
                            }
                        }
                    } catch (Exception ex) {
                        // Handle potential lazy loading exceptions gracefully
                        map.put(field.getName(), "[Lazy/Unloaded]");
                    }
                }
                current = current.getSuperclass();
            }
            return map.toString();
        } catch (Exception e) {
            return "Error serializing details: " + e.getMessage();
        }
    }

    private String getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            // Ignore
        }
        return "SYSTEM";
    }
}
