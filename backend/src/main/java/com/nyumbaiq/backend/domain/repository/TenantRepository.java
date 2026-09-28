package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.Tenant;
import com.nyumbaiq.backend.domain.enums.TenantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Page<Tenant> findByStatus(TenantStatus status, Pageable pageable);
    boolean existsByPhone(String phone);
    Optional<Tenant> findByUserId(UUID userId);
    Page<Tenant> findByUserId(UUID userId, Pageable pageable);
}
