package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateMaintenanceRequestRequest(
        @NotNull UUID propertyId,
        @NotNull UUID unitId,
        @NotBlank @Size(max = 50) String category,
        @NotBlank @Size(max = 255) String title,
        @NotBlank String description,
        String photos
) {
}
