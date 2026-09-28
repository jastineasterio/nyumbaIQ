package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.LockStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SmartLockSummary {
    private UUID id;
    private String lockCode;
    private String name;
    private String unitNumber;
    private LockStatus status;
    private String provider;
    private LocalDateTime lastStatusUpdate;
    private LocalDateTime createdAt;
}
