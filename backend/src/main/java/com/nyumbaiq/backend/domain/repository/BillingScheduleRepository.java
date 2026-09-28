package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.BillingSchedule;
import com.nyumbaiq.backend.domain.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BillingScheduleRepository extends JpaRepository<BillingSchedule, UUID> {
    Page<BillingSchedule> findByLeaseId(UUID leaseId, Pageable pageable);
    Page<BillingSchedule> findByTenantId(UUID tenantId, Pageable pageable);
    Page<BillingSchedule> findByStatus(InvoiceStatus status, Pageable pageable);
    Optional<BillingSchedule> findByLeaseIdAndPeriodStartAndPeriodEnd(UUID leaseId, java.time.LocalDateTime periodStart, java.time.LocalDateTime periodEnd);
}
