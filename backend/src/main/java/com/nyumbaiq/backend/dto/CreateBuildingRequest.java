package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBuildingRequest(
        @NotBlank String name,
        @NotBlank String code,
        @NotBlank String description
) {}
