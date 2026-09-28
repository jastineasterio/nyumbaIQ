package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.CreateUnitRequest;
import com.nyumbaiq.backend.dto.PageResponse;
import com.nyumbaiq.backend.dto.UnitDto;
import com.nyumbaiq.backend.service.UnitService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/floors")
public class UnitController {
    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @GetMapping("/{floorId}/units")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<UnitDto>> getUnits(@PathVariable UUID floorId, Pageable pageable) {
        Page<UnitDto> page = unitService.getUnitsByFloor(floorId, pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @PostMapping("/{floorId}/units")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UnitDto> createUnit(@PathVariable UUID floorId, @Valid @RequestBody CreateUnitRequest request) {
        return ResponseEntity.ok(unitService.createUnit(floorId, request));
    }

    @GetMapping("/units/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UnitDto> getUnit(@PathVariable UUID id) {
        return ResponseEntity.ok(unitService.getUnit(id));
    }

    @PutMapping("/units/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UnitDto> updateUnit(@PathVariable UUID id, @Valid @RequestBody CreateUnitRequest request) {
        return ResponseEntity.ok(unitService.updateUnit(id, request));
    }

    @DeleteMapping("/units/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteUnit(@PathVariable UUID id) {
        unitService.deleteUnit(id);
        return ResponseEntity.noContent().build();
    }
}
