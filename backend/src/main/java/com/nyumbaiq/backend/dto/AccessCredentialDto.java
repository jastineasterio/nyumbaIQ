package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.CredentialStatus;
import com.nyumbaiq.backend.domain.enums.CredentialType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AccessCredentialDto {
    private UUID id;
    private String credentialCode;
    private UUID tenantId;
    private String tenantName;
    private UUID lockId;
    private String lockName;
    private UUID propertyId;
    private String propertyName;
    private UUID unitId;
    private String unitNumber;
    private CredentialType credentialType;
    private CredentialStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private UserSummary createdBy;
    private LocalDateTime createdAt;
}
