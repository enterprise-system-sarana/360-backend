package com.saranaresturantsystem.repository.users;

import com.saranaresturantsystem.entities.users.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}