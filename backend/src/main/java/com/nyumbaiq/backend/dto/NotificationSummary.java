package com.nyumbaiq.backend.dto;

import com.nyumbaiq.backend.domain.enums.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class NotificationSummary {
    private UUID id;
    private String title;
    private String message;
    private NotificationChannel channel;
    private Boolean read;
    private LocalDateTime createdAt;
}
