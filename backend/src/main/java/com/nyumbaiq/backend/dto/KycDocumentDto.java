package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.KycDocumentType;
import com.nyumbaiq.backend.domain.entity.Tenant;
import com.nyumbaiq.backend.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class KycDocumentDto {
    private UUID id;
    private TenantSummary tenant;
    private KycDocumentType documentType;
    private String documentNumber;
    private String fileUrl;
    private String originalFileName;
    private Long fileSize;
    private String contentType;
    private UserSummary uploadedBy;
    private LocalDateTime createdAt;
}
