package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.TenantStatus;

public record UpdateTenantRequest(
        String fullName,
        String phone,
        String email,
        String address,
        String emergencyContact,
        String emergencyPhone,
        TenantStatus status
) {}
