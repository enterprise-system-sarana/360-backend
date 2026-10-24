package com.saranaresturantsystem.audit;

public record AuditLogEvent(
        String tableName,
        String action,
        String recordId,
        String changedBy,
        String changes
) {
}
