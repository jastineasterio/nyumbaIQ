package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.KycStatus;
import jakarta.validation.constraints.NotBlank;

public record ReviewKycDocumentRequest(
        @NotBlank KycStatus status,
        String rejectionReason
) {}
