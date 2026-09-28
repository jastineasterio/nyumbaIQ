package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateInvoiceRequest(
        @NotNull UUID leaseId,
        @NotNull UUID tenantId,
        @NotNull UUID propertyId,
        @NotNull UUID unitId,
        @NotNull UUID billingScheduleId,
        LocalDateTime periodStart,
        LocalDateTime periodEnd,
        LocalDateTime issueDate,
        LocalDateTime dueDate,
        @NotNull java.math.BigDecimal amount,
        String notes
) {}
