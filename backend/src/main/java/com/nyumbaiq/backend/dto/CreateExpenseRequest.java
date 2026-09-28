package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateExpenseRequest(
        @NotNull UUID propertyId,
        UUID buildingId,
        UUID unitId,
        @NotBlank @Size(max = 50) String category,
        @NotBlank String description,
        @NotNull BigDecimal amount,
        @NotNull LocalDateTime expenseDate,
        @Size(max = 255) String vendor,
        @Size(max = 20) String paymentMethod,
        @Size(max = 500) String supportingDocumentPath,
        String notes
) {
}
