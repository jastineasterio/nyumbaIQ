package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.MaintenancePriority;
import jakarta.validation.constraints.Size;

public record UpdateMaintenanceRequestRequest(
        @Size(max = 50) String category,
        @Size(max = 255) String title,
        String description,
        MaintenancePriority priority,
        String photos
) {
}
