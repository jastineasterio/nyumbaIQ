package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateStatementRequest(
        @NotNull UUID tenantId,
        @NotNull UUID propertyId,
        @NotNull UUID unitId,
        @NotNull LocalDateTime periodStart,
        @NotNull LocalDateTime periodEnd
) {}
