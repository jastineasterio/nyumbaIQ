package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.CreateFloorRequest;
import com.nyumbaiq.backend.dto.FloorDto;
import com.nyumbaiq.backend.dto.PageResponse;
import com.nyumbaiq.backend.service.FloorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/buildings")
public class FloorController {
    private final FloorService floorService;

    public FloorController(FloorService floorService) {
        this.floorService = floorService;
    }

    @GetMapping("/{buildingId}/floors")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<FloorDto>> getFloors(@PathVariable UUID buildingId, Pageable pageable) {
        Page<FloorDto> page = floorService.getFloorsByBuilding(buildingId, pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @PostMapping("/{buildingId}/floors")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FloorDto> createFloor(@PathVariable UUID buildingId, @Valid @RequestBody CreateFloorRequest request) {
        return ResponseEntity.ok(floorService.createFloor(buildingId, request));
    }

    @GetMapping("/floors/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FloorDto> getFloor(@PathVariable UUID id) {
        return ResponseEntity.ok(floorService.getFloor(id));
    }

    @PutMapping("/floors/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FloorDto> updateFloor(@PathVariable UUID id, @Valid @RequestBody CreateFloorRequest request) {
        return ResponseEntity.ok(floorService.updateFloor(id, request));
    }

    @DeleteMapping("/floors/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteFloor(@PathVariable UUID id) {
        floorService.deleteFloor(id);
        return ResponseEntity.noContent().build();
    }
}
