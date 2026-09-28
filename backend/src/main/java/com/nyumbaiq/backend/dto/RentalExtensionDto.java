package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.ExtensionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class RentalExtensionDto {
    private UUID id;
    private String extensionNumber;
    private UUID tenantId;
    private String tenantName;
    private UUID leaseId;
    private String leaseNumber;
    private UUID propertyId;
    private String propertyName;
    private UUID unitId;
    private String unitNumber;
    private LocalDateTime originalDueDate;
    private LocalDateTime newDueDate;
    private String reason;
    private ExtensionStatus status;
    private UserSummary approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}
