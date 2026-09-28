package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.MaintenanceCost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MaintenanceCostRepository extends JpaRepository<MaintenanceCost, UUID> {
    Page<MaintenanceCost> findByMaintenanceRequestId(UUID maintenanceRequestId, Pageable pageable);
    Page<MaintenanceCost> findByPropertyId(UUID propertyId, Pageable pageable);
}
