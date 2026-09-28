package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.AccessCredential;
import com.nyumbaiq.backend.domain.enums.CredentialStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccessCredentialRepository extends JpaRepository<AccessCredential, UUID> {
    Page<AccessCredential> findByTenantId(UUID tenantId, Pageable pageable);
    Page<AccessCredential> findByLockId(UUID lockId, Pageable pageable);
    Page<AccessCredential> findByStatus(CredentialStatus status, Pageable pageable);
    Optional<AccessCredential> findByCredentialCode(String credentialCode);
    boolean existsByCredentialCode(String credentialCode);
}
