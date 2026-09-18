package com.yourwifi.audit.repository;

import com.yourwifi.audit.entity.AuditLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findTop10ByOrderByCreatedAtDesc();
}
