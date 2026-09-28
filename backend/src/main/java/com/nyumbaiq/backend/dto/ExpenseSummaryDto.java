package com.nyumbaiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@AllArgsConstructor
public class ExpenseSummaryDto {
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal totalExpenses;
    private BigDecimal approvedExpenses;
    private BigDecimal pendingExpenses;
    private BigDecimal paidExpenses;
    private Map<String, BigDecimal> expensesByCategory;
    private Map<String, BigDecimal> expensesByProperty;
}
