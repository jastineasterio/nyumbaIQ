package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.RentalExtension;
import com.nyumbaiq.backend.domain.enums.ExtensionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RentalExtensionRepository extends JpaRepository<RentalExtension, UUID> {
    Page<RentalExtension> findByTenantId(UUID tenantId, Pageable pageable);
    Page<RentalExtension> findByLeaseId(UUID leaseId, Pageable pageable);
    Page<RentalExtension> findByStatus(ExtensionStatus status, Pageable pageable);
    Optional<RentalExtension> findByExtensionNumber(String extensionNumber);
    boolean existsByExtensionNumber(String extensionNumber);
}
