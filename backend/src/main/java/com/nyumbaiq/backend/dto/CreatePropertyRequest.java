package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePropertyRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotBlank String address,
        @NotBlank String city,
        @NotBlank String region,
        String country
) {}
