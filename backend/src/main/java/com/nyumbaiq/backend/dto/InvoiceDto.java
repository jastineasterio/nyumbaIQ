package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class InvoiceDto {
    private UUID id;
    private String invoiceNumber;
    private UserSummary tenant;
    private PropertySummary property;
    private UnitSummary unit;
    private String leaseNumber;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private BigDecimal amount;
    private BigDecimal amountPaid;
    private BigDecimal balance;
    private InvoiceStatus status;
    private String notes;
    private LocalDateTime createdAt;
}
