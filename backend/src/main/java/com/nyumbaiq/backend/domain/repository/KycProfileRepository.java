package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.KycProfile;
import com.nyumbaiq.backend.domain.enums.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface KycProfileRepository extends JpaRepository<KycProfile, UUID> {
    Optional<KycProfile> findByTenantId(UUID tenantId);
}
