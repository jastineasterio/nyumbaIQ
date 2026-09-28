package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.Floor;
import com.nyumbaiq.backend.domain.enums.FloorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface FloorRepository extends JpaRepository<Floor, UUID> {
    Page<Floor> findByBuildingId(UUID buildingId, Pageable pageable);
    List<Floor> findByBuildingId(UUID buildingId);
    boolean existsByBuildingIdAndFloorNumber(UUID buildingId, Integer floorNumber);
}
