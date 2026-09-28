package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.BillingFrequency;
import com.nyumbaiq.backend.domain.enums.LeaseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateLeaseRequest(
        @NotBlank UUID tenantId,
        @NotBlank UUID unitId,
        @NotBlank UUID propertyId,
        @NotBlank UUID buildingId,
        @NotBlank UUID floorId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        @NotNull BigDecimal rentAmount,
        BigDecimal securityDeposit,
        @NotNull BillingFrequency billingFrequency,
        Integer noticePeriodDays,
        String leaseTerms
) {}
