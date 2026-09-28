package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateExpenseRequest(
        @Size(max = 50) String category,
        String description,
        BigDecimal amount,
        LocalDateTime expenseDate,
        @Size(max = 255) String vendor,
        @Size(max = 20) String paymentMethod,
        @Size(max = 500) String supportingDocumentPath,
        String notes
) {
}
