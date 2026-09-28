package com.nyumbaiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TenantSummary {
    private UUID id;
    private String fullName;
    private String phone;
}
