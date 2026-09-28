package com.nyumbaiq.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessDecisionDto {
    private String decision;
    private String reason;
    private boolean requiresOtp;
    private boolean emergencyAccess;
}
