package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.AccessAction;
import com.nyumbaiq.backend.domain.enums.AccessResult;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AccessEventDto {
    private UUID id;
    private UUID lockId;
    private String lockName;
    private UUID tenantId;
    private String tenantName;
    private UUID credentialId;
    private String credentialCode;
    private UUID propertyId;
    private String propertyName;
    private UUID unitId;
    private String unitNumber;
    private AccessAction action;
    private AccessResult result;
    private String method;
    private String providerReference;
    private String deviceInfo;
    private LocalDateTime timestamp;
    private LocalDateTime createdAt;
}
