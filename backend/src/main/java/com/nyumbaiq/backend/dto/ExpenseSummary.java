package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.ExpenseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ExpenseSummary {
    private UUID id;
    private String expenseNumber;
    private String category;
    private BigDecimal amount;
    private ExpenseStatus status;
    private String propertyName;
    private String unitNumber;
    private String createdBy;
    private LocalDateTime expenseDate;
    private LocalDateTime createdAt;
}
