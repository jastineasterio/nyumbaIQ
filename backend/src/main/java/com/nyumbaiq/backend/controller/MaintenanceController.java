package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.domain.enums.MaintenanceStatus;
import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.service.MaintenanceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {
    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<MaintenanceRequestDto>> getAllMaintenanceRequests(Pageable pageable) {
        Page<MaintenanceRequestDto> page = maintenanceService.getAllMaintenanceRequests(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MaintenanceRequestDto> getMaintenanceRequest(@PathVariable UUID id) {
        return ResponseEntity.ok(maintenanceService.getMaintenanceRequest(id));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MaintenanceRequestDto> createMaintenanceRequest(@Valid @RequestBody CreateMaintenanceRequestRequest request) {
        return ResponseEntity.ok(maintenanceService.createMaintenanceRequest(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<MaintenanceRequestDto> updateMaintenanceRequest(@PathVariable UUID id, @Valid @RequestBody UpdateMaintenanceRequestRequest request) {
        return ResponseEntity.ok(maintenanceService.updateMaintenanceRequest(id, request));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<MaintenanceRequestDto> assignMaintenanceRequest(@PathVariable UUID id, @RequestBody UUID assigneeId) {
        return ResponseEntity.ok(maintenanceService.assignMaintenanceRequest(id, assigneeId));
    }

    @PostMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<MaintenanceRequestDto> updateMaintenanceStatus(@PathVariable UUID id, @RequestBody MaintenanceStatus status) {
        return ResponseEntity.ok(maintenanceService.updateMaintenanceStatus(id, status));
    }

    @PostMapping("/{id}/costs")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<MaintenanceCostDto> addMaintenanceCost(@PathVariable UUID id, @Valid @RequestBody CreateMaintenanceCostRequest request) {
        return ResponseEntity.ok(maintenanceService.addMaintenanceCost(id, request));
    }

    @GetMapping("/{id}/costs")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<MaintenanceCostDto>> getMaintenanceCosts(@PathVariable UUID id, Pageable pageable) {
        Page<MaintenanceCostDto> page = maintenanceService.getMaintenanceCosts(id, pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }
}
