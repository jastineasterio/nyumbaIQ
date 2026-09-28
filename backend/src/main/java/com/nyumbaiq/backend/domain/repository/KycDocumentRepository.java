package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.KycDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.List;

public interface KycDocumentRepository extends JpaRepository<KycDocument, UUID> {
    Page<KycDocument> findByTenantId(UUID tenantId, Pageable pageable);
    List<KycDocument> findByKycProfileId(UUID kycProfileId);
}
