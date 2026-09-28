package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.ExpenseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ExpenseDto {
    private UUID id;
    private String expenseNumber;
    private UUID propertyId;
    private String propertyName;
    private UUID buildingId;
    private String buildingName;
    private UUID unitId;
    private String unitNumber;
    private String category;
    private String description;
    private BigDecimal amount;
    private LocalDateTime expenseDate;
    private String vendor;
    private String paymentMethod;
    private String supportingDocumentPath;
    private String notes;
    private ExpenseStatus status;
    private UserSummary createdBy;
    private UserSummary approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}
