package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.service.LeaseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/leases")
public class LeaseController {
    private final LeaseService leaseService;

    public LeaseController(LeaseService leaseService) {
        this.leaseService = leaseService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<LeaseDto> createLease(@Valid @RequestBody CreateLeaseRequest request) {
        return ResponseEntity.ok(leaseService.createLease(request));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<LeaseDto>> getAllLeases(Pageable pageable) {
        Page<LeaseDto> page = leaseService.getAllLeases(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LeaseDto> getLease(@PathVariable UUID id) {
        return ResponseEntity.ok(leaseService.getLease(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<LeaseDto> updateLease(@PathVariable UUID id, @Valid @RequestBody UpdateLeaseRequest request) {
        return ResponseEntity.ok(leaseService.updateLease(id, request));
    }

    @PostMapping("/{id}/renew")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<LeaseDto> renewLease(@PathVariable UUID id) {
        return ResponseEntity.ok(leaseService.renewLease(id));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<LeaseDto> approveLease(@PathVariable UUID id) {
        return ResponseEntity.ok(leaseService.approveLease(id));
    }

    @PostMapping("/{id}/terminate")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<LeaseDto> terminateLease(@PathVariable UUID id) {
        return ResponseEntity.ok(leaseService.terminateLease(id));
    }

    @GetMapping("/number/{leaseNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LeaseDto> getLeaseByNumber(@PathVariable String leaseNumber) {
        return ResponseEntity.ok(leaseService.getLeaseByNumber(leaseNumber));
    }
}
