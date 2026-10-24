package com.saranaresturantsystem.audit;

import com.saranaresturantsystem.entities.users.AuditLog;
import com.saranaresturantsystem.repository.users.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLogEventHandler {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onAuditEvent(AuditLogEvent event) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .tableName(event.tableName())
                    .action(event.action())
                    .recordId(event.recordId())
                    .changedBy(event.changedBy())
                    .changedAt(LocalDateTime.now())
                    .changes(event.changes())
                    .build();

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.warn("Failed to persist audit log: {}", e.getMessage());
        }
    }
}
