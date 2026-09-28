package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.TenantStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateTenantRequest(
        @NotBlank String fullName,
        @NotBlank String phone,
        @Email String email,
        String address,
        LocalDate dateOfBirth,
        String emergencyContact,
        String emergencyPhone,
        TenantStatus status
) {}
