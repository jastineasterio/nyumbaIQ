package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateUnitRequest(
        @NotBlank String unitNumber,
        @NotBlank String unitType,
        String description
) {}
