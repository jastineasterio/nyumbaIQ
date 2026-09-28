package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.entity.Tenant;
import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.BillingFrequency;
import com.nyumbaiq.backend.domain.enums.LeaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LeaseDto {
    private UUID id;
    private String leaseNumber;
    private UserSummary tenant;
    private PropertySummary property;
    private BuildingSummary building;
    private FloorSummary floor;
    private UnitSummary unit;
    private UserSummary previousTenant;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal rentAmount;
    private BigDecimal securityDeposit;
    private BillingFrequency billingFrequency;
    private Integer noticePeriodDays;
    private String leaseTerms;
    private LeaseStatus status;
    private UserSummary createdBy;
    private UserSummary approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
}
