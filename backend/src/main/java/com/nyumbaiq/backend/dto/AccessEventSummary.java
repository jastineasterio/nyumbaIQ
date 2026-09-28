package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.AccessAction;
import com.nyumbaiq.backend.domain.enums.AccessResult;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AccessEventSummary {
    private UUID id;
    private String lockName;
    private String tenantName;
    private AccessAction action;
    private AccessResult result;
    private String method;
    private LocalDateTime timestamp;
}
