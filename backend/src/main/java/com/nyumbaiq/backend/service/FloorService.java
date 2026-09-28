package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.Building;
import com.nyumbaiq.backend.domain.entity.Floor;
import com.nyumbaiq.backend.domain.enums.FloorStatus;
import com.nyumbaiq.backend.domain.repository.BuildingRepository;
import com.nyumbaiq.backend.domain.repository.FloorRepository;
import com.nyumbaiq.backend.dto.BuildingSummary;
import com.nyumbaiq.backend.dto.CreateFloorRequest;
import com.nyumbaiq.backend.dto.FloorDto;
import com.nyumbaiq.backend.exception.ConflictException;
import com.nyumbaiq.backend.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class FloorService {
    private final FloorRepository floorRepository;
    private final BuildingRepository buildingRepository;

    public FloorService(FloorRepository floorRepository, BuildingRepository buildingRepository) {
        this.floorRepository = floorRepository;
        this.buildingRepository = buildingRepository;
    }

    @Transactional(readOnly = true)
    public Page<FloorDto> getFloorsByBuilding(UUID buildingId, Pageable pageable) {
        return floorRepository.findByBuildingId(buildingId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public FloorDto getFloor(UUID id) {
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Floor not found"));
        return toDto(floor);
    }

    public FloorDto createFloor(UUID buildingId, CreateFloorRequest request) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new NotFoundException("Building not found"));

        if (floorRepository.existsByBuildingIdAndFloorNumber(buildingId, request.floorNumber())) {
            throw new ConflictException("Floor number already exists for this building");
        }

        Floor floor = Floor.builder()
                .name(request.name())
                .floorNumber(request.floorNumber())
                .building(building)
                .description(request.description())
                .status(FloorStatus.ACTIVE)
                .build();
        floorRepository.save(floor);
        return toDto(floor);
    }

    public FloorDto updateFloor(UUID id, CreateFloorRequest request) {
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Floor not found"));

        if (request.name() != null) floor.setName(request.name());
        if (request.floorNumber() != null) floor.setFloorNumber(request.floorNumber());
        if (request.description() != null) floor.setDescription(request.description());

        floorRepository.save(floor);
        return toDto(floor);
    }

    public void deleteFloor(UUID id) {
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Floor not found"));
        floorRepository.delete(floor);
    }

    private FloorDto toDto(Floor floor) {
        Building building = floor.getBuilding();
        return new FloorDto(
                floor.getId(),
                floor.getName(),
                floor.getFloorNumber(),
                new BuildingSummary(building.getId(), building.getName(), building.getCode()),
                floor.getDescription(),
                floor.getStatus(),
                floor.getCreatedAt()
        );
    }
}
