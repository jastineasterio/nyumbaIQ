package com.nyumbaiq.backend.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OutstandingTenantDto(
        UUID tenantId,
        String tenantName,
        String propertyName,
        String unitNumber,
        BigDecimal outstandingAmount,
        Integer overdueDays
) {
}
