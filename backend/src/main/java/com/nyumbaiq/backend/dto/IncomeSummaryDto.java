package com.nyumbaiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
public class IncomeSummaryDto {
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal totalIncome;
    private BigDecimal expectedIncome;
    private BigDecimal collectionRate;
    private Map<String, BigDecimal> incomeBySource;
    private Map<String, BigDecimal> incomeByProperty;
}
