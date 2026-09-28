package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
        @NotNull UUID tenantId,
        UUID leaseId,
        UUID propertyId,
        @NotNull BigDecimal amount,
        @NotBlank String currency,
        @NotNull PaymentMethod paymentMethod,
        String provider,
        String providerTransactionReference,
        String internalTransactionReference
) {}
