package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.BillingScheduleDto;
import com.nyumbaiq.backend.dto.CreateBillingScheduleRequest;
import com.nyumbaiq.backend.dto.PageResponse;
import com.nyumbaiq.backend.service.BillingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/billing")
public class BillingController {
    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<Void> generateBilling() {
        billingService.generateBilling();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/schedules")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<BillingScheduleDto>> getAllSchedules(Pageable pageable) {
        Page<BillingScheduleDto> page = billingService.getAllSchedules(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @GetMapping("/schedules/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BillingScheduleDto> getSchedule(@PathVariable UUID id) {
        return ResponseEntity.ok(billingService.getSchedule(id));
    }
}
