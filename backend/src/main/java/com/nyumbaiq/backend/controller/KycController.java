package com.nyumbaiq.backend.controller;

import com.nyumbaiq.backend.dto.KycDocumentDto;
import com.nyumbaiq.backend.dto.KycProfileDto;
import com.nyumbaiq.backend.dto.PageResponse;
import com.nyumbaiq.backend.dto.ReviewKycDocumentRequest;
import com.nyumbaiq.backend.dto.UploadKycDocumentRequest;
import com.nyumbaiq.backend.service.KycService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/kyc")
public class KycController {
    private final KycService kycService;

    public KycController(KycService kycService) {
        this.kycService = kycService;
    }

    @GetMapping("/tenant/{tenantId}/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KycProfileDto> getProfile(@PathVariable UUID tenantId) {
        return ResponseEntity.ok(kycService.getKycProfile(tenantId));
    }

    @PostMapping("/tenant/{tenantId}/documents")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KycDocumentDto> uploadDocument(@PathVariable UUID tenantId, @Valid UploadKycDocumentRequest request) {
        return ResponseEntity.ok(kycService.uploadDocument(tenantId, request));
    }

    @GetMapping("/tenant/{tenantId}/documents")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PageResponse<KycDocumentDto>> getDocuments(@PathVariable UUID tenantId, Pageable pageable) {
        Page<KycDocumentDto> page = kycService.getDocuments(tenantId, pageable);
        return ResponseEntity.ok(new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }

    @GetMapping("/documents/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KycDocumentDto> getDocument(@PathVariable UUID id) {
        return ResponseEntity.ok(kycService.getDocument(id));
    }

    @PutMapping("/documents/{id}/review")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    public ResponseEntity<KycDocumentDto> reviewDocument(@PathVariable UUID id, @Valid @RequestBody ReviewKycDocumentRequest request) {
        return ResponseEntity.ok(kycService.reviewDocument(id, request));
    }

    @DeleteMapping("/documents/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        kycService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }
}
