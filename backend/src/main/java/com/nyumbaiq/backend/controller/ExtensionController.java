package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.service.ExtensionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/extensions")
public class ExtensionController {
    private final ExtensionService extensionService;

    public ExtensionController(ExtensionService extensionService) {
        this.extensionService = extensionService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<RentalExtensionDto>> getAllExtensions(Pageable pageable) {
        Page<RentalExtensionDto> page = extensionService.getAllExtensions(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RentalExtensionDto> getExtension(@PathVariable UUID id) {
        return ResponseEntity.ok(extensionService.getExtension(id));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RentalExtensionDto> createExtension(@Valid @RequestBody CreateRentalExtensionRequest request) {
        return ResponseEntity.ok(extensionService.createExtension(request));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<RentalExtensionDto> approveExtension(@PathVariable UUID id, @RequestBody ReviewRentalExtensionRequest request) {
        return ResponseEntity.ok(extensionService.approveExtension(id, request));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<RentalExtensionDto> rejectExtension(@PathVariable UUID id, @RequestBody ReviewRentalExtensionRequest request) {
        return ResponseEntity.ok(extensionService.rejectExtension(id, request));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RentalExtensionDto> cancelExtension(@PathVariable UUID id) {
        return ResponseEntity.ok(extensionService.cancelExtension(id));
    }
}
