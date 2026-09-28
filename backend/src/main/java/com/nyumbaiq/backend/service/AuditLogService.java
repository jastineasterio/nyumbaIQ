package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.AuditLog;
import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.AuditResult;
import com.nyumbaiq.backend.domain.repository.AuditLogRepository;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final CurrentUser currentUser;

    public AuditLogService(AuditLogRepository auditLogRepository, CurrentUser currentUser) {
        this.auditLogRepository = auditLogRepository;
        this.currentUser = currentUser;
    }

    public void record(String action, String entityType, String entityId, AuditResult result) {
        UUID actorId = null;
        try {
            actorId = currentUser.getUserId();
        } catch (Exception e) {
            // Not authenticated
        }

        AuditLog log = AuditLog.builder()
                .actor(actorId != null ? User.builder().id(actorId).build() : null)
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .timestamp(LocalDateTime.now())
                .ipAddress(currentUser.getIpAddress())
                .userAgent(currentUser.getUserAgent())
                .result(result)
                .build();
        auditLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getAuditLogsByActor(UUID actorId, Pageable pageable) {
        return auditLogRepository.findByActorId(actorId, pageable);
    }
}
