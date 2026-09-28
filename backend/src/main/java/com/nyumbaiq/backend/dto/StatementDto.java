package com.nyumbaiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class StatementDto {
    private UUID id;
    private String statementNumber;
    private UserSummary tenant;
    private PropertySummary property;
    private UnitSummary unit;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private LocalDateTime generatedAt;
    private LocalDateTime createdAt;
}
