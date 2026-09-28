package com.nyumbaiq.backend.event;

import com.nyumbaiq.backend.domain.enums.AuditResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class AuditEventMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String action;
    private String entityType;
    private String entityId;
    private AuditResult result;
    private String ipAddress;
    private String userAgent;
}
