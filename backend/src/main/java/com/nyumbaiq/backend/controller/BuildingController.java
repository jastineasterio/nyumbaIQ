package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.BuildingDto;
import com.nyumbaiq.backend.dto.CreateBuildingRequest;
import com.nyumbaiq.backend.dto.PageResponse;
import com.nyumbaiq.backend.service.BuildingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/properties")
public class BuildingController {
    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping("/{propertyId}/buildings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<BuildingDto>> getBuildings(@PathVariable UUID propertyId, Pageable pageable) {
        Page<BuildingDto> page = buildingService.getBuildingsByProperty(propertyId, pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @PostMapping("/{propertyId}/buildings")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BuildingDto> createBuilding(@PathVariable UUID propertyId, @Valid @RequestBody CreateBuildingRequest request) {
        return ResponseEntity.ok(buildingService.createBuilding(propertyId, request));
    }

    @GetMapping("/buildings/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BuildingDto> getBuilding(@PathVariable UUID id) {
        return ResponseEntity.ok(buildingService.getBuilding(id));
    }

    @PutMapping("/buildings/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BuildingDto> updateBuilding(@PathVariable UUID id, @Valid @RequestBody CreateBuildingRequest request) {
        return ResponseEntity.ok(buildingService.updateBuilding(id, request));
    }

    @DeleteMapping("/buildings/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteBuilding(@PathVariable UUID id) {
        buildingService.deleteBuilding(id);
        return ResponseEntity.noContent().build();
    }
}
