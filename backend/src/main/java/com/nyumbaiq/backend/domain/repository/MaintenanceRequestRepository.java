package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.MaintenanceRequest;
import com.nyumbaiq.backend.domain.enums.MaintenancePriority;
import com.nyumbaiq.backend.domain.enums.MaintenanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, UUID> {
    Page<MaintenanceRequest> findByTenantId(UUID tenantId, Pageable pageable);
    Page<MaintenanceRequest> findByPropertyId(UUID propertyId, Pageable pageable);
    Page<MaintenanceRequest> findByPropertyIdAndStatus(UUID propertyId, MaintenanceStatus status, Pageable pageable);
    Page<MaintenanceRequest> findByAssignedToId(UUID assignedToId, Pageable pageable);
    Page<MaintenanceRequest> findByStatus(MaintenanceStatus status, Pageable pageable);
    Optional<MaintenanceRequest> findByRequestNumber(String requestNumber);
    boolean existsByRequestNumber(String requestNumber);
}
