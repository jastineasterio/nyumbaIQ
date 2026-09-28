package com.nyumbaiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class FloorSummary {
    private UUID id;
    private String name;
    private Integer floorNumber;
}
