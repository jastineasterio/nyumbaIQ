package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.Notification;
import com.nyumbaiq.backend.domain.entity.Property;
import com.nyumbaiq.backend.domain.entity.Tenant;
import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.NotificationChannel;
import com.nyumbaiq.backend.domain.repository.NotificationRepository;
import com.nyumbaiq.backend.domain.repository.TenantRepository;
import com.nyumbaiq.backend.domain.repository.UserRepository;
import com.nyumbaiq.backend.dto.NotificationDto;
import com.nyumbaiq.backend.dto.NotificationSummary;
import com.nyumbaiq.backend.event.NotificationEventMessage;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;
    private final RabbitTemplate rabbitTemplate;

    public NotificationService(NotificationRepository notificationRepository, TenantRepository tenantRepository,
                               UserRepository userRepository, CurrentUser currentUser, RabbitTemplate rabbitTemplate) {
        this.notificationRepository = notificationRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.currentUser = currentUser;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotifications(Pageable pageable) {
        UUID userId = currentUser.getUserId();
        return notificationRepository.findByUserId(userId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {
        UUID userId = currentUser.getUserId();
        return notificationRepository.countByUserIdAndRead(userId, Boolean.FALSE);
    }

    public NotificationDto markAsRead(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        UUID userId = currentUser.getUserId();
        if (!userId.equals(notification.getUser() != null ? notification.getUser().getId() : null)) {
            throw new AccessDeniedException("You are not allowed to access this notification");
        }
        notification.setRead(Boolean.TRUE);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
        return toDto(notification);
    }

    public void markAllAsRead() {
        UUID userId = currentUser.getUserId();
        Page<Notification> notifications = notificationRepository.findByUserId(userId, Pageable.unpaged());
        for (Notification notification : notifications.getContent()) {
            if (!Boolean.TRUE.equals(notification.getRead())) {
                notification.setRead(Boolean.TRUE);
                notification.setReadAt(LocalDateTime.now());
                notificationRepository.save(notification);
            }
        }
    }

    public void sendNotification(UUID userId, UUID tenantId, UUID propertyId, NotificationChannel channel,
                                 String type, String title, String message) {
        String notificationNumber = "NOTIF-" + System.currentTimeMillis();
        Notification notification = Notification.builder()
                .notificationNumber(notificationNumber)
                .user(userId != null ? User.builder().id(userId).build() : null)
                .tenant(tenantId != null ? Tenant.builder().id(tenantId).build() : null)
                .property(propertyId != null ? Property.builder().id(propertyId).build() : null)
                .channel(channel)
                .type(type)
                .title(title)
                .message(message)
                .read(Boolean.FALSE)
                .build();
        notificationRepository.save(notification);

        NotificationEventMessage event = NotificationEventMessage.builder()
                .notificationNumber(notificationNumber)
                .userId(userId)
                .tenantId(tenantId)
                .propertyId(propertyId)
                .channel(channel)
                .type(type)
                .title(title)
                .message(message)
                .build();
        try {
            rabbitTemplate.convertAndSend("nyumbaiq.exchange", "notification.event", event);
        } catch (Exception e) {
        }
    }

    private NotificationDto toDto(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getNotificationNumber(),
                notification.getUser() != null ? notification.getUser().getId() : null,
                notification.getTenant() != null ? notification.getTenant().getId() : null,
                notification.getTenant() != null ? notification.getTenant().getFullName() : null,
                notification.getProperty() != null ? notification.getProperty().getId() : null,
                notification.getProperty() != null ? notification.getProperty().getName() : null,
                notification.getChannel(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getData(),
                notification.getRead(),
                notification.getReadAt(),
                notification.getCreatedAt()
        );
    }
}
