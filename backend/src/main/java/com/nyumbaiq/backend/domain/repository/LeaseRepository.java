package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.Lease;
import com.nyumbaiq.backend.domain.enums.LeaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LeaseRepository extends JpaRepository<Lease, UUID> {
    Page<Lease> findByTenantId(UUID tenantId, Pageable pageable);
    Page<Lease> findByPropertyId(UUID propertyId, Pageable pageable);
    Page<Lease> findByPropertyIdAndStatus(UUID propertyId, LeaseStatus status, Pageable pageable);
    Page<Lease> findByStatus(LeaseStatus status, Pageable pageable);
    Optional<Lease> findByUnitIdAndStatus(UUID unitId, LeaseStatus status);
    Optional<Lease> findByLeaseNumber(String leaseNumber);
    boolean existsByLeaseNumber(String leaseNumber);
    boolean existsByUnitIdAndStatus(UUID unitId, LeaseStatus status);
}
