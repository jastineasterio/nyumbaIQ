package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.entity.Building;
import com.nyumbaiq.backend.domain.enums.FloorStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class FloorDto {
    private UUID id;
    private String name;
    private Integer floorNumber;
    private BuildingSummary building;
    private String description;
    private FloorStatus status;
    private LocalDateTime createdAt;
}
