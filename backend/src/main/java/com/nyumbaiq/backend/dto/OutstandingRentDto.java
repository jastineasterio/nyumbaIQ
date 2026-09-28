package com.nyumbaiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OutstandingRentDto {
    private BigDecimal totalOutstanding;
    private long totalTenants;
    private List<OutstandingTenantDto> tenants;
}
