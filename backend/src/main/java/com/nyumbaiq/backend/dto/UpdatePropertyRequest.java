package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.PropertyStatus;

public record UpdatePropertyRequest(
        String name,
        String description,
        String address,
        String city,
        String region,
        String country,
        PropertyStatus status
) {}
