package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.MaintenanceService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CreateMaintenanceRequestTool implements AiTool {
    private final MaintenanceService maintenanceService;

    public CreateMaintenanceRequestTool(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @Override
    public String name() {
        return "create_maintenance_request";
    }

    @Override
    public String description() {
        return "Create a new maintenance request for a property unit.";
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
        Object title = parameters.get("title");
        Object unitId = parameters.get("unitId");
        return "Create maintenance request: " + (title != null ? title : "untitled") + " for unit " + (unitId != null ? unitId : "unknown") + "?";
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);

            UUID propertyId = extractUuid(parameters, "propertyId");
            UUID unitId = extractUuid(parameters, "unitId");
            String title = extractString(parameters, "title");
            String description = extractString(parameters, "description");

            if (propertyId == null || unitId == null || title == null || title.isBlank()) {
                return new AiToolResult(null, name(), "Property ID, Unit ID, and title are required.", false);
            }

            com.nyumbaiq.backend.dto.CreateMaintenanceRequestRequest request =
                    new com.nyumbaiq.backend.dto.CreateMaintenanceRequestRequest(
                            propertyId,
                            unitId,
                            "GENERAL",
                            title,
                            description != null ? description : "",
                            null
                    );

            var created = maintenanceService.createMaintenanceRequest(request);

            return new AiToolResult(null, name(), "Maintenance request created successfully with number: " + (created.getRequestNumber() != null ? created.getRequestNumber() : "N/A"), true);
        } catch (NotFoundException e) {
            return new AiToolResult(null, name(), "Property or unit not found.", false);
        } catch (AccessDeniedException e) {
            return new AiToolResult(null, name(), "Access denied.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to create maintenance request.", false);
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

    private String extractString(Map<String, Object> parameters, String key) {
        Object param = parameters.get(key);
        return param != null ? param.toString() : null;
    }
}
