package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.AuditLog;
import com.nyumbaiq.backend.domain.enums.AuditResult;
import com.nyumbaiq.backend.domain.repository.AuditLogRepository;
import com.nyumbaiq.backend.security.CurrentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {
    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private AuditLogService auditLogService;

    @Test
    void record_ShouldSaveAuditLog() {
        auditLogService.record("TEST_ACTION", "TEST_ENTITY", "entity-1", AuditResult.SUCCESS);
        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void getAuditLogs_ShouldReturnPage() {
        Page<AuditLog> page = new PageImpl<>(List.of(AuditLog.builder().action("TEST").build()));
        when(auditLogRepository.findAll(any(Pageable.class))).thenReturn(page);
        var result = auditLogService.getAuditLogs(org.springframework.data.domain.PageRequest.of(0, 20));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAuditLogsByActor_ShouldReturnPage() {
        Page<AuditLog> page = new PageImpl<>(List.of(AuditLog.builder().action("TEST").build()));
        when(auditLogRepository.findByActorId(any(), any())).thenReturn(page);
        var result = auditLogService.getAuditLogsByActor(UUID.randomUUID(), org.springframework.data.domain.PageRequest.of(0, 20));
        assertEquals(1, result.getTotalElements());
    }
}
