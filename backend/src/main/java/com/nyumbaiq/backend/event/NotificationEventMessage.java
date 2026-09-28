package com.nyumbaiq.backend.event;

import com.nyumbaiq.backend.domain.enums.NotificationChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class NotificationEventMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String notificationNumber;
    private UUID userId;
    private UUID tenantId;
    private UUID propertyId;
    private NotificationChannel channel;
    private String type;
    private String title;
    private String message;
    private String data;
}
