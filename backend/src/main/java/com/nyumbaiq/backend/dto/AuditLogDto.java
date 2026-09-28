package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.AuditResult;
import com.nyumbaiq.backend.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AuditLogDto {
    private UUID id;
    private UserSummary actor;
    private String action;
    private String entityType;
    private String entityId;
    private LocalDateTime timestamp;
    private String ipAddress;
    private AuditResult result;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}
