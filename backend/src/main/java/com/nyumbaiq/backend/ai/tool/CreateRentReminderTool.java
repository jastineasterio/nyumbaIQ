package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.NotificationChannel;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.NotificationService;
import com.nyumbaiq.backend.service.TenantService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CreateRentReminderTool implements AiTool {
    private final NotificationService notificationService;
    private final TenantService tenantService;

    public CreateRentReminderTool(NotificationService notificationService, TenantService tenantService) {
        this.notificationService = notificationService;
        this.tenantService = tenantService;
    }

    @Override
    public String name() {
        return "create_rent_reminder";
    }

    @Override
    public String description() {
        return "Send a rent reminder notification to a tenant.";
    }

    @Override
    public List<String> requiredParameters() {
        return List.of();
    }

    @Override
    public boolean requiresConfirmation() {
        return true;
    }

    @Override
    public String confirmationMessage(Map<String, Object> parameters) {
        Object tenantId = parameters.get("tenantId");
        return "Send rent reminder to tenant " + (tenantId != null ? tenantId.toString() : "specified tenant") + "?";
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);
            if (ctx.getRole() == Role.TENANT) {
                return new AiToolResult(null, name(), "Tenants cannot send rent reminders.", false);
            }

            UUID tenantId = extractTenantId(parameters);
            if (tenantId == null) {
                return new AiToolResult(null, name(), "Tenant ID is required.", false);
            }

            var tenant = tenantService.getTenant(tenantId);

            notificationService.sendNotification(
                    tenant.getUser() != null ? tenant.getUser().getId() : tenantId,
                    tenantId,
                    null,
                    NotificationChannel.IN_APP,
                    "RENT_REMINDER",
                    "Rent Reminder",
                    "This is a friendly reminder that your rent payment is due soon."
            );

            return new AiToolResult(null, name(), "Rent reminder sent successfully to " + (tenant.getFullName() != null ? tenant.getFullName() : "tenant"), true);
        } catch (NotFoundException e) {
            return new AiToolResult(null, name(), "Tenant not found.", false);
        } catch (AccessDeniedException e) {
            return new AiToolResult(null, name(), "Access denied.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to send rent reminder.", false);
        }
    }

    private UUID extractTenantId(Map<String, Object> parameters) {
        Object param = parameters.get("tenantId");
        if (param instanceof String s) {
            return UUID.fromString(s);
        }
        if (param instanceof UUID uuid) {
            return uuid;
        }
        return null;
    }
}
