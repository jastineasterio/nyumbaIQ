package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.AccessEvent;
import com.nyumbaiq.backend.domain.enums.AccessAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccessEventRepository extends JpaRepository<AccessEvent, UUID> {
    Page<AccessEvent> findByLockId(UUID lockId, Pageable pageable);
    Page<AccessEvent> findByTenantId(UUID tenantId, Pageable pageable);
    Page<AccessEvent> findByAction(AccessAction action, Pageable pageable);
    Page<AccessEvent> findByPropertyId(UUID propertyId, Pageable pageable);
}
