package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateRentalExtensionRequest(
        @NotNull UUID leaseId,
        @NotNull LocalDateTime originalDueDate,
        @NotNull LocalDateTime newDueDate,
        @NotBlank String reason
) {
}
