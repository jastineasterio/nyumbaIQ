package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class NotificationDto {
    private UUID id;
    private String notificationNumber;
    private UUID userId;
    private UUID tenantId;
    private String tenantName;
    private UUID propertyId;
    private String propertyName;
    private NotificationChannel channel;
    private String type;
    private String title;
    private String message;
    private String data;
    private Boolean read;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
