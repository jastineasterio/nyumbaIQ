package com.nyumbaiq.backend.access;

import com.nyumbaiq.backend.domain.entity.*;
import com.nyumbaiq.backend.domain.enums.*;
import com.nyumbaiq.backend.domain.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class AccessPolicyEngine {
    private final LeaseRepository leaseRepository;
    private final InvoiceRepository invoiceRepository;
    private final RentalExtensionRepository extensionRepository;

    public AccessPolicyEngine(LeaseRepository leaseRepository, InvoiceRepository invoiceRepository,
                              RentalExtensionRepository extensionRepository) {
        this.leaseRepository = leaseRepository;
        this.invoiceRepository = invoiceRepository;
        this.extensionRepository = extensionRepository;
    }

    public AccessDecision evaluateAccess(Tenant tenant, Unit unit) {
        if (tenant == null || unit == null) {
            return AccessDecision.restricted("Tenant or unit not found");
        }

        Lease activeLease = findActiveLease(tenant.getId(), unit.getId());
        if (activeLease == null || activeLease.getStatus() != LeaseStatus.ACTIVE) {
            return AccessDecision.restricted("No active lease found for tenant and unit");
        }

        if (tenant.getStatus() == TenantStatus.SUSPENDED) {
            return AccessDecision.suspended("Tenant account is suspended");
        }

        Page<Invoice> allInvoices = invoiceRepository.findByTenantId(tenant.getId(), Pageable.unpaged());
        boolean hasOverdue = allInvoices.getContent().stream()
                .anyMatch(inv -> inv.getStatus() == InvoiceStatus.OVERDUE);
        if (hasOverdue) {
            return AccessDecision.restricted("Tenant has overdue invoices");
        }

        if (hasApprovedExtension(tenant.getId())) {
            return AccessDecision.allowed("Access allowed via approved extension");
        }

        return AccessDecision.allowed("Access allowed");
    }

    public AccessDecision evaluateEmergencyAccess(Tenant tenant, Unit unit) {
        if (tenant == null || unit == null) {
            return AccessDecision.restricted("Tenant or unit not found");
        }
        return AccessDecision.emergencyAccess("Emergency access granted");
    }

    public AccessDecision evaluateMaintenanceAccess(Tenant tenant, Unit unit) {
        if (tenant == null || unit == null) {
            return AccessDecision.restricted("Tenant or unit not found");
        }
        return AccessDecision.maintenanceAccess("Maintenance access granted");
    }

    private Lease findActiveLease(UUID tenantId, UUID unitId) {
        Page<Lease> leases = leaseRepository.findByTenantId(tenantId, Pageable.unpaged());
        for (Lease lease : leases) {
            if (lease.getUnit() != null && lease.getUnit().getId().equals(unitId)
                    && lease.getStatus() == LeaseStatus.ACTIVE) {
                return lease;
            }
        }
        return null;
    }

    private boolean hasApprovedExtension(UUID tenantId) {
        Page<RentalExtension> extensions = extensionRepository.findByTenantId(tenantId, Pageable.unpaged());
        return extensions.getContent().stream()
                .anyMatch(ext -> ext.getStatus() == ExtensionStatus.APPROVED);
    }
}
