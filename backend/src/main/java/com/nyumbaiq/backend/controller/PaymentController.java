package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.*;
import com.nyumbaiq.backend.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentDto> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPayment(request));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<PaymentDto>> getAllPayments(Pageable pageable) {
        Page<PaymentDto> page = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentDto> getPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    @PostMapping("/{id}/allocate")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<PaymentDto> allocatePayment(@PathVariable UUID id, @Valid @RequestBody CreatePaymentAllocationRequest request) {
        return ResponseEntity.ok(paymentService.allocatePayment(id, request));
    }

    @PostMapping("/verify")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<PaymentDto> verifyPayment(@RequestParam String providerTransactionReference) {
        return ResponseEntity.ok(paymentService.verifyPayment(providerTransactionReference));
    }

    @GetMapping("/reference/{reference}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentDto> getPaymentByReference(@PathVariable String reference) {
        return ResponseEntity.ok(paymentService.getPaymentByReference(reference));
    }
}
