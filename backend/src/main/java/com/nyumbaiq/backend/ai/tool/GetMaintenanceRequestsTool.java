package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.MaintenanceStatus;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.MaintenanceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GetMaintenanceRequestsTool implements AiTool {
    private final MaintenanceService maintenanceService;

    public GetMaintenanceRequestsTool(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @Override
    public String name() {
        return "get_maintenance_requests";
    }

    @Override
    public String description() {
        return "Retrieve maintenance requests, filtered by tenant or property scope.";
    }

    @Override
    public List<String> requiredParameters() {
        return List.of();
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);
            UUID tenantId = null;
            if (ctx.getRole() == Role.TENANT) {
                tenantId = ctx.getUserId();
            } else {
                Object param = parameters.get("tenantId");
                if (param instanceof String s) {
                    tenantId = UUID.fromString(s);
                } else if (param instanceof UUID uuid) {
                    tenantId = uuid;
                }
            }

            Page<com.nyumbaiq.backend.dto.MaintenanceRequestDto> requests = maintenanceService.getAllMaintenanceRequests(
                    PageRequest.of(0, 20)
            );

            StringBuilder sb = new StringBuilder();
            sb.append("Maintenance Requests:\n");
            int count = 0;
            for (var r : requests.getContent()) {
                if (tenantId != null && !tenantId.equals(r.getTenantId())) {
                    continue;
                }
                sb.append(String.format("- %s | %s | Priority: %s | Status: %s | Property: %s%n",
                        r.getRequestNumber() != null ? r.getRequestNumber() : "N/A",
                        r.getTitle() != null ? r.getTitle() : "N/A",
                        r.getPriority() != null ? r.getPriority() : "N/A",
                        r.getStatus() != null ? r.getStatus() : "N/A",
                        r.getPropertyName() != null ? r.getPropertyName() : "N/A"
                ));
                count++;
            }

            if (count == 0) {
                sb.append("No maintenance requests found.");
            }

            return new AiToolResult(null, name(), sb.toString(), true);
        } catch (AccessDeniedException | NotFoundException e) {
            return new AiToolResult(null, name(), "Access denied or resource not found.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve maintenance requests.", false);
        }
    }
}
