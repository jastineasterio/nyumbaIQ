package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.LeaseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateLeaseRequest(
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal rentAmount,
        BigDecimal securityDeposit,
        Integer noticePeriodDays,
        String leaseTerms,
        LeaseStatus status
) {}
