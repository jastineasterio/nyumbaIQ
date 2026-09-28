package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.Unit;
import com.nyumbaiq.backend.domain.enums.UnitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, UUID> {
    Page<Unit> findByFloorId(UUID floorId, Pageable pageable);
    List<Unit> findByFloorId(UUID floorId);
    Page<Unit> findByPropertyId(UUID propertyId, Pageable pageable);
    Page<Unit> findByPropertyIdAndStatus(UUID propertyId, UnitStatus status, Pageable pageable);
    long countByStatus(UnitStatus status);
    long countByPropertyIdAndStatus(UUID propertyId, UnitStatus status);
    Optional<Unit> findByPropertyIdAndBuildingIdAndFloorIdAndUnitNumber(UUID propertyId, UUID buildingId, UUID floorId, String unitNumber);
}
