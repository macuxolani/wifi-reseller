package com.yourwifi.audit.service;

import com.yourwifi.audit.entity.AuditLog;
import com.yourwifi.audit.repository.AuditLogRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AuditLog recordAudit(
        UUID adminUserId,
        String action,
        String entityType,
        String entityId,
        String previousValue,
        String newValue,
        String ipAddress,
        String userAgent
    ) {
        AuditLog log = new AuditLog();
        log.setId(UUID.randomUUID());
        log.setAdminUserId(adminUserId);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPreviousValue(previousValue);
        log.setNewValue(newValue);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setCreatedAt(Instant.now());
        return auditLogRepository.save(log);
    }

    public List<AuditLog> listRecentEvents() {
        return auditLogRepository.findTop10ByOrderByCreatedAtDesc();
    }
}
