package com.nyumbaiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ReceiptSummary {
    private UUID id;
    private String receiptNumber;
    private String tenantName;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDateTime issuedAt;
}
