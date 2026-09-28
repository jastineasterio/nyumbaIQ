package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateMaintenanceCostRequest(
        @NotNull UUID maintenanceRequestId,
        @Size(max = 50) String category,
        String description,
        @NotNull BigDecimal labourCost,
        @NotNull BigDecimal materialsCost,
        @NotNull BigDecimal vendorCost,
        @Size(max = 255) String vendor,
        String notes
) {
}
