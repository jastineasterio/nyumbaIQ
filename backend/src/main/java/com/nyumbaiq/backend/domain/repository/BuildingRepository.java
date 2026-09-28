package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.Building;
import com.nyumbaiq.backend.domain.enums.BuildingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, UUID> {
    Page<Building> findByPropertyId(UUID propertyId, Pageable pageable);
    List<Building> findByPropertyId(UUID propertyId);
    boolean existsByPropertyIdAndCode(UUID propertyId, String code);
}
