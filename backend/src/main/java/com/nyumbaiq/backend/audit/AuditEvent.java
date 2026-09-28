package com.nyumbaiq.backend.audit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuditEvent {
    private String action;
    private String entityType;
    private String entityId;
    private String ipAddress;
    private String userAgent;
}
