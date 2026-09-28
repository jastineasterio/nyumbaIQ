package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.AuditLogDto;
import com.nyumbaiq.backend.dto.PageResponse;
import com.nyumbaiq.backend.service.AuditLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {
    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PageResponse<AuditLogDto>> getAuditLogs(Pageable pageable) {
        Page<com.nyumbaiq.backend.domain.entity.AuditLog> page = auditLogService.getAuditLogs(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent().stream().map(log -> new AuditLogDto(
                log.getId(),
                log.getActor() != null ? new com.nyumbaiq.backend.dto.UserSummary(log.getActor().getId(), log.getActor().getFirstName(), log.getActor().getLastName(), log.getActor().getEmail(), log.getActor().getRole(), log.getActor().getStatus()) : null,
                log.getAction(),
                log.getEntityType(),
                log.getEntityId(),
                log.getTimestamp(),
                log.getIpAddress(),
                log.getResult(),
                log.getMetadata(),
                log.getCreatedAt()
        )).toList(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }
}
