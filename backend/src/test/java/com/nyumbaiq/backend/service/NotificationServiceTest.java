package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.domain.entity.Notification;
import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.domain.enums.NotificationChannel;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.domain.enums.UserStatus;
import com.nyumbaiq.backend.domain.repository.NotificationRepository;
import com.nyumbaiq.backend.dto.NotificationDto;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void getNotifications_ShouldReturnPage() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .firstName("Test")
                .lastName("User")
                .email("user@test.com")
                .role(Role.TENANT)
                .status(UserStatus.ACTIVE)
                .build();
        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .notificationNumber("NOTIF-123")
                .user(user)
                .channel(NotificationChannel.IN_APP)
                .type("TEST")
                .title("Test")
                .message("Test message")
                .read(false)
                .build();
        when(currentUser.getUserId()).thenReturn(userId);
        Page<Notification> page = new PageImpl<>(List.of(notification));
        when(notificationRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(page);
        var result = notificationService.getNotifications(Pageable.ofSize(20));
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .firstName("Test")
                .lastName("User")
                .email("user@test.com")
                .role(Role.TENANT)
                .status(UserStatus.ACTIVE)
                .build();
        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .notificationNumber("NOTIF-123")
                .user(user)
                .channel(NotificationChannel.IN_APP)
                .type("TEST")
                .title("Test")
                .message("Test message")
                .read(false)
                .build();
        when(currentUser.getUserId()).thenReturn(userId);
        when(notificationRepository.findById(notification.getId())).thenReturn(Optional.of(notification));
        NotificationDto dto = notificationService.markAsRead(notification.getId());
        assertTrue(dto.getRead());
    }
}
