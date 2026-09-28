package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateBillingScheduleRequest(
        @NotNull UUID leaseId,
        LocalDateTime periodStart,
        LocalDateTime periodEnd,
        LocalDateTime chargeDate,
        LocalDateTime dueDate,
        @NotNull java.math.BigDecimal amount
) {}
