package com.nyumbaiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class MaintenanceCostDto {
    private UUID id;
    private UUID maintenanceRequestId;
    private UUID propertyId;
    private String propertyName;
    private UUID unitId;
    private String unitNumber;
    private String category;
    private String description;
    private BigDecimal labourCost;
    private BigDecimal materialsCost;
    private BigDecimal vendorCost;
    private BigDecimal totalCost;
    private String vendor;
    private String notes;
    private LocalDateTime createdAt;
}
