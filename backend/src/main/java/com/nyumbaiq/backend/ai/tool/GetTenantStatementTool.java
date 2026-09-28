package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.StatementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GetTenantStatementTool implements AiTool {
    private final StatementService statementService;

    public GetTenantStatementTool(StatementService statementService) {
        this.statementService = statementService;
    }

    @Override
    public String name() {
        return "get_tenant_statement";
    }

    @Override
    public String description() {
        return "Retrieve the latest statement for a tenant.";
    }

    @Override
    public List<String> requiredParameters() {
        return List.of();
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);

            Page<com.nyumbaiq.backend.dto.StatementDto> statements = statementService.getAllStatements(
                    PageRequest.of(0, 5, Sort.by("generatedAt").descending())
            );

            StringBuilder sb = new StringBuilder();
            sb.append("Recent Statements:\n");
            int count = 0;
            for (var s : statements.getContent()) {
                if (ctx.getRole() == Role.TENANT && s.getTenant() != null && !s.getTenant().getId().equals(ctx.getUserId())) {
                    continue;
                }
                sb.append(String.format("- %s | Property: %s | Opening: %s | Closing: %s | Generated: %s%n",
                        s.getStatementNumber() != null ? s.getStatementNumber() : "N/A",
                        s.getProperty() != null && s.getProperty().getName() != null ? s.getProperty().getName() : "N/A",
                        s.getOpeningBalance() != null ? s.getOpeningBalance() : "N/A",
                        s.getClosingBalance() != null ? s.getClosingBalance() : "N/A",
                        s.getGeneratedAt() != null ? s.getGeneratedAt().toString() : "N/A"
                ));
                count++;
            }

            if (count == 0) {
                sb.append("No statements found.");
            }

            return new AiToolResult(null, name(), sb.toString(), true);
        } catch (AccessDeniedException | NotFoundException e) {
            return new AiToolResult(null, name(), "Access denied or resource not found.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve tenant statement.", false);
        }
    }
}
