package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.PaymentMethod;
import com.nyumbaiq.backend.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentDto {
    private UUID id;
    private String paymentReference;
    private UserSummary tenant;
    private PropertySummary property;
    private String leaseNumber;
    private BigDecimal amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private String provider;
    private String providerTransactionReference;
    private String internalTransactionReference;
    private PaymentStatus status;
    private LocalDateTime initiatedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime createdAt;
}
