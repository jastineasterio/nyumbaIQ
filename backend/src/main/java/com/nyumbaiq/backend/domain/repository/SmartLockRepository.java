package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.SmartLock;
import com.nyumbaiq.backend.domain.enums.LockStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SmartLockRepository extends JpaRepository<SmartLock, UUID> {
    Page<SmartLock> findByPropertyId(UUID propertyId, Pageable pageable);
    Page<SmartLock> findByUnitId(UUID unitId, Pageable pageable);
    Page<SmartLock> findByStatus(LockStatus status, Pageable pageable);
    Optional<SmartLock> findByLockCode(String lockCode);
    Optional<SmartLock> findByUnitIdAndStatus(UUID unitId, LockStatus status);
    boolean existsByLockCode(String lockCode);
}
