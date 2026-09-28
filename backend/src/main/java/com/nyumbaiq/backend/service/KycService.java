package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.KycDocument;
import com.nyumbaiq.backend.domain.entity.KycProfile;
import com.nyumbaiq.backend.domain.entity.Tenant;
import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.KycDocumentType;
import com.nyumbaiq.backend.domain.enums.KycStatus;
import com.nyumbaiq.backend.domain.repository.KycDocumentRepository;
import com.nyumbaiq.backend.domain.repository.KycProfileRepository;
import com.nyumbaiq.backend.domain.repository.TenantRepository;
import com.nyumbaiq.backend.dto.KycDocumentDto;
import com.nyumbaiq.backend.dto.KycProfileDto;
import com.nyumbaiq.backend.dto.ReviewKycDocumentRequest;
import com.nyumbaiq.backend.dto.TenantSummary;
import com.nyumbaiq.backend.dto.UploadKycDocumentRequest;
import com.nyumbaiq.backend.dto.UserSummary;
import com.nyumbaiq.backend.exception.ConflictException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class KycService {
    private final KycProfileRepository kycProfileRepository;
    private final KycDocumentRepository kycDocumentRepository;
    private final TenantRepository tenantRepository;
    private final CurrentUser currentUser;

    public KycService(KycProfileRepository kycProfileRepository, KycDocumentRepository kycDocumentRepository,
                      TenantRepository tenantRepository, CurrentUser currentUser) {
        this.kycProfileRepository = kycProfileRepository;
        this.kycDocumentRepository = kycDocumentRepository;
        this.tenantRepository = tenantRepository;
        this.currentUser = currentUser;
    }

    @Transactional(readOnly = true)
    public KycProfileDto getKycProfile(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));
        KycProfile profile = kycProfileRepository.findByTenantId(tenantId)
                .orElseGet(() -> createKycProfile(tenant));
        return toDto(profile);
    }

    public KycDocumentDto uploadDocument(UUID tenantId, UploadKycDocumentRequest request) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));

        MultipartFile file = request.file();
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String uploadDir = "./uploads/kyc/tenant/" + tenantId;
        Path uploadPath = Paths.get(uploadDir);
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create upload directory", e);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String storedFilename = UUID.randomUUID() + extension;
        Path destination = uploadPath.resolve(storedFilename);

        try {
            Files.copy(file.getInputStream(), destination);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }

        KycProfile profile = kycProfileRepository.findByTenantId(tenantId)
                .orElseGet(() -> createKycProfile(tenant));

        User uploadedBy = new com.nyumbaiq.backend.domain.entity.User();
        uploadedBy.setId(currentUser.getUserId());

        KycDocument document = KycDocument.builder()
                .tenant(tenant)
                .kycProfile(profile)
                .documentType(request.documentType())
                .documentNumber(request.documentNumber())
                .filePath(destination.toString())
                .originalFileName(originalFilename)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .uploadedBy(uploadedBy)
                .build();
        kycDocumentRepository.save(document);
        return toDto(document);
    }

    public KycDocumentDto reviewDocument(UUID documentId, ReviewKycDocumentRequest request) {
        KycDocument document = kycDocumentRepository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Document not found"));

        UUID reviewerId = currentUser.getUserId();
        User reviewer = new com.nyumbaiq.backend.domain.entity.User();
        reviewer.setId(reviewerId);

        KycProfile profile = document.getKycProfile();
        profile.setStatus(request.status());
        profile.setReviewedBy(reviewer);
        profile.setReviewedAt(LocalDateTime.now());
        if (request.rejectionReason() != null) {
            profile.setRejectionReason(request.rejectionReason());
        }
        kycProfileRepository.save(profile);

        return toDto(document);
    }

    @Transactional(readOnly = true)
    public Page<KycDocumentDto> getDocuments(UUID tenantId, Pageable pageable) {
        return kycDocumentRepository.findByTenantId(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public KycDocumentDto getDocument(UUID id) {
        KycDocument document = kycDocumentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found"));
        return toDto(document);
    }

    public void deleteDocument(UUID id) {
        KycDocument document = kycDocumentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found"));
        try {
            Files.deleteIfExists(Paths.get(document.getFilePath()));
        } catch (IOException e) {
            // Log but continue
        }
        kycDocumentRepository.delete(document);
    }

    private KycProfile createKycProfile(Tenant tenant) {
        KycProfile profile = KycProfile.builder()
                .tenant(tenant)
                .status(KycStatus.PENDING)
                .build();
        return kycProfileRepository.save(profile);
    }

    private KycProfileDto toDto(KycProfile profile) {
        User reviewedBy = profile.getReviewedBy();
        return new KycProfileDto(
                profile.getId(),
                new TenantSummary(profile.getTenant().getId(), profile.getTenant().getFullName(), profile.getTenant().getPhone()),
                profile.getStatus(),
                reviewedBy != null ? new UserSummary(reviewedBy.getId(), reviewedBy.getFirstName(), reviewedBy.getLastName(), reviewedBy.getEmail(), reviewedBy.getRole(), reviewedBy.getStatus()) : null,
                profile.getReviewedAt(),
                profile.getRejectionReason(),
                profile.getExpiryDate()
        );
    }

    private KycDocumentDto toDto(KycDocument document) {
        User uploadedBy = document.getUploadedBy();
        return new KycDocumentDto(
                document.getId(),
                new TenantSummary(document.getTenant().getId(), document.getTenant().getFullName(), document.getTenant().getPhone()),
                document.getDocumentType(),
                document.getDocumentNumber(),
                document.getFilePath(),
                document.getOriginalFileName(),
                document.getFileSize(),
                document.getContentType(),
                uploadedBy != null ? new UserSummary(uploadedBy.getId(), uploadedBy.getFirstName(), uploadedBy.getLastName(), uploadedBy.getEmail(), uploadedBy.getRole(), uploadedBy.getStatus()) : null,
                document.getCreatedAt()
        );
    }
}
