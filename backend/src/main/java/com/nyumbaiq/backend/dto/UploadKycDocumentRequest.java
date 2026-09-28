package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.KycDocumentType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record UploadKycDocumentRequest(
        @NotBlank KycDocumentType documentType,
        String documentNumber,
        @NotBlank MultipartFile file
) {}
