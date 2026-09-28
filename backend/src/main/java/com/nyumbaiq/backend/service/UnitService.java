package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.Building;
import com.nyumbaiq.backend.domain.entity.Floor;
import com.nyumbaiq.backend.domain.entity.Property;
import com.nyumbaiq.backend.domain.entity.Unit;
import com.nyumbaiq.backend.domain.enums.UnitStatus;
import com.nyumbaiq.backend.domain.repository.FloorRepository;
import com.nyumbaiq.backend.domain.repository.PropertyRepository;
import com.nyumbaiq.backend.domain.repository.UnitRepository;
import com.nyumbaiq.backend.dto.BuildingSummary;
import com.nyumbaiq.backend.dto.CreateUnitRequest;
import com.nyumbaiq.backend.dto.FloorSummary;
import com.nyumbaiq.backend.dto.PropertySummary;
import com.nyumbaiq.backend.dto.UnitDto;
import com.nyumbaiq.backend.exception.ConflictException;
import com.nyumbaiq.backend.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UnitService {
    private final UnitRepository unitRepository;
    private final FloorRepository floorRepository;
    private final PropertyRepository propertyRepository;

    public UnitService(UnitRepository unitRepository, FloorRepository floorRepository, PropertyRepository propertyRepository) {
        this.unitRepository = unitRepository;
        this.floorRepository = floorRepository;
        this.propertyRepository = propertyRepository;
    }

    @Transactional(readOnly = true)
    public Page<UnitDto> getUnitsByFloor(UUID floorId, Pageable pageable) {
        return unitRepository.findByFloorId(floorId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public UnitDto getUnit(UUID id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unit not found"));
        return toDto(unit);
    }

    public UnitDto createUnit(UUID floorId, CreateUnitRequest request) {
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new NotFoundException("Floor not found"));
        Building building = floor.getBuilding();
        Property property = building.getProperty();

        Unit existing = unitRepository.findByPropertyIdAndBuildingIdAndFloorIdAndUnitNumber(
                property.getId(), building.getId(), floor.getId(), request.unitNumber())
                .orElse(null);
        if (existing != null) {
            throw new ConflictException("Unit number already exists");
        }

        Unit unit = Unit.builder()
                .unitNumber(request.unitNumber())
                .floor(floor)
                .building(building)
                .property(property)
                .unitType(request.unitType())
                .description(request.description())
                .status(UnitStatus.VACANT)
                .build();
        unitRepository.save(unit);
        return toDto(unit);
    }

    public UnitDto updateUnit(UUID id, CreateUnitRequest request) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unit not found"));

        if (request.unitNumber() != null) unit.setUnitNumber(request.unitNumber());
        if (request.unitType() != null) unit.setUnitType(request.unitType());
        if (request.description() != null) unit.setDescription(request.description());

        unitRepository.save(unit);
        return toDto(unit);
    }

    public void deleteUnit(UUID id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unit not found"));
        unitRepository.delete(unit);
    }

    private UnitDto toDto(Unit unit) {
        return new UnitDto(
                unit.getId(),
                unit.getUnitNumber(),
                new FloorSummary(unit.getFloor().getId(), unit.getFloor().getName(), unit.getFloor().getFloorNumber()),
                new BuildingSummary(unit.getBuilding().getId(), unit.getBuilding().getName(), unit.getBuilding().getCode()),
                new PropertySummary(unit.getProperty().getId(), unit.getProperty().getName(), unit.getProperty().getPropertyCode()),
                unit.getUnitType(),
                unit.getDescription(),
                unit.getStatus(),
                unit.getCreatedAt()
        );
    }
}
