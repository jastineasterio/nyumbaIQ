package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.entity.Tenant;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ReceiptDto {
    private UUID id;
    private String receiptNumber;
    private UserSummary tenant;
    private PropertySummary property;
    private UnitSummary unit;
    private BigDecimal amount;
    private BigDecimal previousBalance;
    private BigDecimal remainingBalance;
    private String paymentMethod;
    private String provider;
    private String transactionReference;
    private LocalDateTime issuedAt;
    private LocalDateTime createdAt;
}
