package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.service.SmartLockService;
import com.nyumbaiq.backend.access.AccessDecision;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/smart-locks")
public class SmartLockController {
    private final SmartLockService smartLockService;

    public SmartLockController(SmartLockService smartLockService) {
        this.smartLockService = smartLockService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<PageResponse<SmartLockDto>> getAllSmartLocks(Pageable pageable) {
        Page<SmartLockDto> page = smartLockService.getAllSmartLocks(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<SmartLockDto> getSmartLock(@PathVariable UUID id) {
        return ResponseEntity.ok(smartLockService.getSmartLock(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<SmartLockDto> createSmartLock(@Valid @RequestBody CreateSmartLockRequest request) {
        return ResponseEntity.ok(smartLockService.createSmartLock(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<SmartLockDto> updateSmartLock(@PathVariable UUID id, @Valid @RequestBody UpdateSmartLockRequest request) {
        return ResponseEntity.ok(smartLockService.updateSmartLock(id, request));
    }

    @PostMapping("/{id}/lock")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<SmartLockDto> lock(@PathVariable UUID id) {
        return ResponseEntity.ok(smartLockService.lock(id));
    }

    @PostMapping("/{id}/unlock")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<SmartLockDto> unlock(@PathVariable UUID id) {
        return ResponseEntity.ok(smartLockService.unlock(id));
    }

    @PostMapping("/{id}/credentials")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<AccessCredentialDto> createCredential(@PathVariable UUID id, @Valid @RequestBody CreateAccessCredentialRequest request) {
        return ResponseEntity.ok(smartLockService.createCredential(id, request));
    }

    @GetMapping("/{id}/credentials")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<PageResponse<AccessCredentialDto>> getCredentials(@PathVariable UUID id, Pageable pageable) {
        Page<AccessCredentialDto> page = smartLockService.getCredentials(id, pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @PostMapping("/{id}/revoke-credential/{credentialId}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<AccessCredentialDto> revokeCredential(@PathVariable UUID id, @PathVariable UUID credentialId) {
        return ResponseEntity.ok(smartLockService.revokeCredential(id, credentialId));
    }

    @GetMapping("/{id}/events")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<PageResponse<AccessEventDto>> getAccessEvents(@PathVariable UUID id, Pageable pageable) {
        Page<AccessEventDto> page = smartLockService.getAccessEvents(id, pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @GetMapping("/{id}/access-policy")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<AccessDecisionDto> getAccessPolicy(@PathVariable UUID id) {
        return ResponseEntity.ok(new AccessDecisionDto("ALLOWED", "Default policy", false, false));
    }
}
