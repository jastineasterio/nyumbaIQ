package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.entity.Tenant;
import com.nyumbaiq.backend.domain.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BillingScheduleDto {
    private UUID id;
    private String leaseNumber;
    private String tenantName;
    private String unitNumber;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private LocalDateTime chargeDate;
    private LocalDateTime dueDate;
    private BigDecimal amount;
    private InvoiceStatus status;
    private LocalDateTime createdAt;
}
