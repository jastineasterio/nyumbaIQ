package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.Size;

public record UpdateSmartLockRequest(
        @Size(max = 255) String name,
        @Size(max = 50) String provider
) {
}
