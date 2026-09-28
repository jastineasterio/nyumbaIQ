package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.LockStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SmartLockDto {
    private UUID id;
    private String lockCode;
    private String name;
    private UUID propertyId;
    private String propertyName;
    private UUID buildingId;
    private String buildingName;
    private UUID unitId;
    private String unitNumber;
    private String provider;
    private String providerLockId;
    private LockStatus status;
    private LocalDateTime lastStatusUpdate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
