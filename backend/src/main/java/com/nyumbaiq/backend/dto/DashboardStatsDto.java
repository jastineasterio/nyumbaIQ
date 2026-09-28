package com.nyumbaiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DashboardStatsDto {
    private long totalProperties;
    private long totalBuildings;
    private long totalFloors;
    private long totalUnits;
    private long occupiedUnits;
    private long vacantUnits;
    private long totalManagers;
    private long totalTenants;
}
