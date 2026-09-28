package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentAllocationRequest(
        @NotNull UUID paymentId,
        @NotNull UUID invoiceId,
        @NotNull BigDecimal amount
) {}
