package com.nyumbaiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PaymentAllocationDto {
    private UUID id;
    private PaymentSummary payment;
    private String invoiceNumber;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
