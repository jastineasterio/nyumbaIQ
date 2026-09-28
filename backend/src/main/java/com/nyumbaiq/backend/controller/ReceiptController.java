package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.PageResponse;
import com.nyumbaiq.backend.dto.ReceiptDto;
import com.nyumbaiq.backend.service.ReceiptService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {
    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<ReceiptDto>> getAllReceipts(Pageable pageable) {
        Page<ReceiptDto> page = receiptService.getAllReceipts(pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReceiptDto> getReceipt(@PathVariable UUID id) {
        return ResponseEntity.ok(receiptService.getReceipt(id));
    }

    @GetMapping("/number/{receiptNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReceiptDto> getReceiptByNumber(@PathVariable String receiptNumber) {
        return ResponseEntity.ok(receiptService.getReceiptByNumber(receiptNumber));
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable UUID id) {
        byte[] content = receiptService.downloadReceipt(id);
        return ResponseEntity.ok()
                .header("Content-Type", "text/plain")
                .header("Content-Disposition", "attachment; filename=receipt-" + id + ".txt")
                .body(content);
    }
}
