package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFloorRequest(
        @NotBlank String name,
        @NotBlank Integer floorNumber,
        @NotBlank String description
) {}
