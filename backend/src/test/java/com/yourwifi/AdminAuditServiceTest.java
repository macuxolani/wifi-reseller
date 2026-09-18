package com.yourwifi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yourwifi.admin.dto.AdminOverviewResponse;
import com.yourwifi.admin.repository.AdminUserRepository;
import com.yourwifi.admin.service.AdminService;
import com.yourwifi.audit.entity.AuditLog;
import com.yourwifi.audit.repository.AuditLogRepository;
import com.yourwifi.audit.service.AuditService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminAuditServiceTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AdminService adminService;

    @InjectMocks
    private AuditService auditService;

    @Test
    void adminOverviewReturnsCountsFromRepositories() {
        when(adminUserRepository.count()).thenReturn(3L);

        AdminOverviewResponse overview = adminService.getOverview();

        assertNotNull(overview);
        assertEquals(3, overview.totalAdmins());
    }

    @Test
    void auditServiceCreatesAuditEntry() {
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var log = auditService.recordAudit(
            UUID.randomUUID(),
            "USER_LOGIN",
            "customer",
            UUID.randomUUID().toString(),
            "before",
            "after",
            "127.0.0.1",
            "JUnit"
        );

        assertNotNull(log);
        assertEquals("USER_LOGIN", log.getAction());
        assertNotNull(log.getCreatedAt());
    }

    @Test
    void auditServiceListsLatestEvents() {
        when(auditLogRepository.findTop10ByOrderByCreatedAtDesc()).thenReturn(List.of(new AuditLog()));
        var logs = auditService.listRecentEvents();
        assertEquals(1, logs.size());
    }
}
