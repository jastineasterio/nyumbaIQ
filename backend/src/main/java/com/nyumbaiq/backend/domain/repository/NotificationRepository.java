package com.nyumbaiq.backend.domain.repository;

import com.nyumbaiq.backend.domain.entity.Notification;
import com.nyumbaiq.backend.domain.enums.NotificationChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserId(UUID userId, Pageable pageable);
    Page<Notification> findByTenantId(UUID tenantId, Pageable pageable);
    Page<Notification> findByTenantIdAndRead(UUID tenantId, Boolean read, Pageable pageable);
    Page<Notification> findByPropertyId(UUID propertyId, Pageable pageable);
    Page<Notification> findByChannel(NotificationChannel channel, Pageable pageable);
    long countByTenantIdAndRead(UUID tenantId, Boolean read);
    long countByUserIdAndRead(UUID userId, Boolean read);
}
