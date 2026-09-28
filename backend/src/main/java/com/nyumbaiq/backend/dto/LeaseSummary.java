package com.nyumbaiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LeaseSummary {
    private UUID id;
    private String leaseNumber;
    private String tenantName;
    private String unitNumber;
    private BigDecimal rentAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
