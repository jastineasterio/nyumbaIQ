package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GetNotificationsTool implements AiTool {
    private final NotificationService notificationService;

    public GetNotificationsTool(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public String name() {
        return "get_notifications";
    }

    @Override
    public String description() {
        return "Retrieve the latest notifications for the current user.";
    }

    @Override
    public List<String> requiredParameters() {
        return List.of();
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            Page<com.nyumbaiq.backend.dto.NotificationDto> notifications = notificationService.getNotifications(
                    PageRequest.of(0, 10)
            );

            StringBuilder sb = new StringBuilder();
            sb.append("Recent Notifications:\n");
            int count = 0;
            for (var n : notifications.getContent()) {
                sb.append(String.format("- %s | %s | Read: %s%n",
                        n.getTitle() != null ? n.getTitle() : "N/A",
                        n.getMessage() != null ? n.getMessage() : "N/A",
                        n.getRead() != null ? n.getRead() : false
                ));
                count++;
            }

            if (count == 0) {
                sb.append("No notifications found.");
            }

            return new AiToolResult(null, name(), sb.toString(), true);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve notifications.", false);
        }
    }
}
