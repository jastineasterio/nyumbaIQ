package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.entity.Property;
import com.nyumbaiq.backend.domain.enums.BuildingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class BuildingDto {
    private UUID id;
    private String name;
    private String code;
    private PropertySummary property;
    private String description;
    private BuildingStatus status;
    private LocalDateTime createdAt;
}
