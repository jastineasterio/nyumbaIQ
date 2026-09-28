package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.entity.Building;
import com.nyumbaiq.backend.domain.entity.Floor;
import com.nyumbaiq.backend.domain.entity.Property;
import com.nyumbaiq.backend.domain.enums.UnitStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UnitDto {
    private UUID id;
    private String unitNumber;
    private FloorSummary floor;
    private BuildingSummary building;
    private PropertySummary property;
    private String unitType;
    private String description;
    private UnitStatus status;
    private LocalDateTime createdAt;
}
