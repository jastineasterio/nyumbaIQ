package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateSmartLockRequest(
        @NotBlank @Size(max = 50) String lockCode,
        @NotBlank @Size(max = 255) String name,
        @NotNull UUID propertyId,
        @NotNull UUID buildingId,
        @NotNull UUID unitId,
        @Size(max = 50) String provider
) {
}
