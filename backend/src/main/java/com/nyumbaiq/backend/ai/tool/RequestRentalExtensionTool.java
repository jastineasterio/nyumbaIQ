package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.ExtensionService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class RequestRentalExtensionTool implements AiTool {
    private final ExtensionService extensionService;

    public RequestRentalExtensionTool(ExtensionService extensionService) {
        this.extensionService = extensionService;
    }

    @Override
    public String name() {
        return "request_rental_extension";
    }

    @Override
    public String description() {
        return "Request a rental extension for a specific lease.";
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
        Object leaseId = parameters.get("leaseId");
        Object newDueDate = parameters.get("newDueDate");
        return "Request rental extension for lease " + (leaseId != null ? leaseId : "unknown") + " with new due date " + (newDueDate != null ? newDueDate : "unknown") + "?";
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);

            UUID leaseId = extractUuid(parameters, "leaseId");
            LocalDateTime originalDueDate = extractDateTime(parameters, "originalDueDate");
            LocalDateTime newDueDate = extractDateTime(parameters, "newDueDate");
            String reason = extractString(parameters, "reason");

            if (leaseId == null || originalDueDate == null || newDueDate == null) {
                return new AiToolResult(null, name(), "Lease ID, originalDueDate, and newDueDate are required.", false);
            }

            com.nyumbaiq.backend.dto.CreateRentalExtensionRequest request =
                    new com.nyumbaiq.backend.dto.CreateRentalExtensionRequest(
                            leaseId,
                            originalDueDate,
                            newDueDate,
                            reason != null ? reason : ""
                    );

            var extension = extensionService.createExtension(request);

            return new AiToolResult(null, name(), "Rental extension requested successfully with number: " + (extension.getExtensionNumber() != null ? extension.getExtensionNumber() : "N/A"), true);
        } catch (NotFoundException e) {
            return new AiToolResult(null, name(), "Lease not found.", false);
        } catch (AccessDeniedException e) {
            return new AiToolResult(null, name(), "Access denied.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to request rental extension.", false);
        }
    }

    private UUID extractUuid(Map<String, Object> parameters, String key) {
        Object param = parameters.get(key);
        if (param instanceof String s) {
            return UUID.fromString(s);
        }
        if (param instanceof UUID uuid) {
            return uuid;
        }
        return null;
    }

    private LocalDateTime extractDateTime(Map<String, Object> parameters, String key) {
        Object param = parameters.get(key);
        if (param instanceof LocalDateTime dt) {
            return dt;
        }
        if (param instanceof String s) {
            return LocalDateTime.parse(s);
        }
        return null;
    }

    private String extractString(Map<String, Object> parameters, String key) {
        Object param = parameters.get(key);
        return param != null ? param.toString() : null;
    }
}
