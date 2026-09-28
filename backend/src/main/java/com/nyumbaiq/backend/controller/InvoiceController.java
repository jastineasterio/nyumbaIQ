package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.InvoiceDto;
import com.nyumbaiq.backend.dto.PageResponse;
import com.nyumbaiq.backend.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<InvoiceDto>> getAllInvoices(Pageable pageable) {
        Page<InvoiceDto> page = invoiceService.getAllInvoices(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InvoiceDto> getInvoice(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.getInvoice(id));
    }

    @GetMapping("/number/{invoiceNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InvoiceDto> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(invoiceService.getInvoiceByNumber(invoiceNumber));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<InvoiceDto> cancelInvoice(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.cancelInvoice(id));
    }
}
