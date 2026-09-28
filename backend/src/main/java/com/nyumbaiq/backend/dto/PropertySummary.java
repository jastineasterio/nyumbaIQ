package com.nyumbaiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PropertySummary {
    private UUID id;
    private String name;
    private String propertyCode;
}
