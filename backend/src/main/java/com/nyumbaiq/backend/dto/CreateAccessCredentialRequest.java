package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.CredentialType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAccessCredentialRequest(
        @NotNull UUID tenantId,
        @NotNull UUID lockId,
        @NotNull CredentialType credentialType,
        LocalDateTime expiresAt,
        @Size(max = 50) String credentialCode
) {
}
