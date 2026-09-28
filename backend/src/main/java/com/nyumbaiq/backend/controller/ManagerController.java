package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.CreateManagerRequest;
import com.nyumbaiq.backend.dto.ManagerDto;
import com.nyumbaiq.backend.dto.PageResponse;
import com.nyumbaiq.backend.service.ManagerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/managers")
public class ManagerController {
    private final ManagerService managerService;

    public ManagerController(ManagerService managerService) {
        this.managerService = managerService;
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PageResponse<ManagerDto>> getAllManagers(Pageable pageable) {
        Page<ManagerDto> page = managerService.getAllManagers(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ManagerDto> createManager(@Valid @RequestBody CreateManagerRequest request) {
        return ResponseEntity.ok(managerService.createManager(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ManagerDto> getManager(@PathVariable UUID id) {
        return ResponseEntity.ok(managerService.getManager(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ManagerDto> updateManager(@PathVariable UUID id, @Valid @RequestBody CreateManagerRequest request) {
        return ResponseEntity.ok(managerService.updateManager(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteManager(@PathVariable UUID id) {
        managerService.deleteManager(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/assign-property")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ManagerDto> assignProperty(@PathVariable UUID id, @RequestBody UUID propertyId) {
        return ResponseEntity.ok(managerService.assignProperty(id, propertyId));
    }

    @DeleteMapping("/{id}/assignments/{assignmentId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> removeAssignment(@PathVariable UUID id, @PathVariable UUID assignmentId) {
        managerService.removeAssignment(id, assignmentId);
        return ResponseEntity.noContent().build();
    }
}
