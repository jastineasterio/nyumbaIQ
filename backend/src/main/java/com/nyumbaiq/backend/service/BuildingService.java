package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.Building;
import com.nyumbaiq.backend.domain.entity.Property;
import com.nyumbaiq.backend.domain.enums.BuildingStatus;
import com.nyumbaiq.backend.domain.repository.BuildingRepository;
import com.nyumbaiq.backend.domain.repository.PropertyRepository;
import com.nyumbaiq.backend.dto.BuildingDto;
import com.nyumbaiq.backend.dto.CreateBuildingRequest;
import com.nyumbaiq.backend.dto.PropertySummary;
import com.nyumbaiq.backend.exception.ConflictException;
import com.nyumbaiq.backend.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class BuildingService {
    private final BuildingRepository buildingRepository;
    private final PropertyRepository propertyRepository;

    public BuildingService(BuildingRepository buildingRepository, PropertyRepository propertyRepository) {
        this.buildingRepository = buildingRepository;
        this.propertyRepository = propertyRepository;
    }

    @Transactional(readOnly = true)
    public Page<BuildingDto> getBuildingsByProperty(UUID propertyId, Pageable pageable) {
        return buildingRepository.findByPropertyId(propertyId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public BuildingDto getBuilding(UUID id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Building not found"));
        return toDto(building);
    }

    public BuildingDto createBuilding(UUID propertyId, CreateBuildingRequest request) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new NotFoundException("Property not found"));

        if (buildingRepository.existsByPropertyIdAndCode(propertyId, request.code())) {
            throw new ConflictException("Building code already exists for this property");
        }

        Building building = Building.builder()
                .name(request.name())
                .code(request.code())
                .property(property)
                .description(request.description())
                .status(BuildingStatus.ACTIVE)
                .build();
        buildingRepository.save(building);
        return toDto(building);
    }

    public BuildingDto updateBuilding(UUID id, CreateBuildingRequest request) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Building not found"));

        if (request.name() != null) building.setName(request.name());
        if (request.code() != null) building.setCode(request.code());
        if (request.description() != null) building.setDescription(request.description());

        buildingRepository.save(building);
        return toDto(building);
    }

    public void deleteBuilding(UUID id) {
        Building building = buildingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Building not found"));
        buildingRepository.delete(building);
    }

    private BuildingDto toDto(Building building) {
        Property property = building.getProperty();
        return new BuildingDto(
                building.getId(),
                building.getName(),
                building.getCode(),
                new PropertySummary(property.getId(), property.getName(), property.getPropertyCode()),
                building.getDescription(),
                building.getStatus(),
                building.getCreatedAt()
        );
    }
}
