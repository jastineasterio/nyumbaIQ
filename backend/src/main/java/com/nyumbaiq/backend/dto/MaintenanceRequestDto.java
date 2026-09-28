package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.MaintenancePriority;
import com.nyumbaiq.backend.domain.enums.MaintenanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class MaintenanceRequestDto {
    private UUID id;
    private String requestNumber;
    private UUID tenantId;
    private String tenantName;
    private UUID propertyId;
    private String propertyName;
    private UUID unitId;
    private String unitNumber;
    private String category;
    private String title;
    private String description;
    private MaintenancePriority priority;
    private MaintenanceStatus status;
    private UUID assignedToId;
    private String assignedToName;
    private String photos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
