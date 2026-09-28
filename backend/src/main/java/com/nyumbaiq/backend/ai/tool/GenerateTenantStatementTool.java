package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.StatementService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GenerateTenantStatementTool implements AiTool {
    private final StatementService statementService;

    public GenerateTenantStatementTool(StatementService statementService) {
        this.statementService = statementService;
    }

    @Override
    public String name() {
        return "generate_tenant_statement";
    }

    @Override
    public String description() {
        return "Generate a financial statement for a tenant over a specific period.";
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
        Object periodStart = parameters.get("periodStart");
        Object periodEnd = parameters.get("periodEnd");
        return "Generate statement for tenant " + (tenantId != null ? tenantId : "unknown") + " from " + (periodStart != null ? periodStart : "unknown") + " to " + (periodEnd != null ? periodEnd : "unknown") + "?";
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);
            if (ctx.getRole() == Role.TENANT) {
                return new AiToolResult(null, name(), "Tenants cannot generate statements.", false);
            }

            UUID tenantId = extractUuid(parameters, "tenantId");
            LocalDateTime periodStart = extractDateTime(parameters, "periodStart");
            LocalDateTime periodEnd = extractDateTime(parameters, "periodEnd");
            UUID propertyId = extractUuid(parameters, "propertyId");
            UUID unitId = extractUuid(parameters, "unitId");

            if (tenantId == null || periodStart == null || periodEnd == null || propertyId == null || unitId == null) {
                return new AiToolResult(null, name(), "Tenant ID, Property ID, Unit ID, periodStart, and periodEnd are required.", false);
            }

            com.nyumbaiq.backend.dto.CreateStatementRequest request =
                    new com.nyumbaiq.backend.dto.CreateStatementRequest(
                            tenantId,
                            propertyId,
                            unitId,
                            periodStart,
                            periodEnd
                    );

            var statement = statementService.generateStatement(request);

            return new AiToolResult(null, name(), "Statement generated successfully with number: " + (statement.getStatementNumber() != null ? statement.getStatementNumber() : "N/A"), true);
        } catch (NotFoundException e) {
            return new AiToolResult(null, name(), "Tenant or property not found.", false);
        } catch (AccessDeniedException e) {
            return new AiToolResult(null, name(), "Access denied.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to generate tenant statement.", false);
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
}
