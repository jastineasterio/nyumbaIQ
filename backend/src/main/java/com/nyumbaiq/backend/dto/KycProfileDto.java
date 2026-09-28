package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.KycStatus;
import com.nyumbaiq.backend.domain.entity.Tenant;
import com.nyumbaiq.backend.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class KycProfileDto {
    private UUID id;
    private TenantSummary tenant;
    private KycStatus status;
    private UserSummary reviewedBy;
    private LocalDateTime reviewedAt;
    private String rejectionReason;
    private LocalDate expiryDate;
}
