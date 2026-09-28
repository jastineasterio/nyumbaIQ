package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.MaintenancePriority;
import com.nyumbaiq.backend.domain.enums.MaintenanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class MaintenanceRequestSummary {
    private UUID id;
    private String requestNumber;
    private String tenantName;
    private String propertyName;
    private String unitNumber;
    private String title;
    private MaintenancePriority priority;
    private MaintenanceStatus status;
    private String assignedToName;
    private LocalDateTime createdAt;
}
