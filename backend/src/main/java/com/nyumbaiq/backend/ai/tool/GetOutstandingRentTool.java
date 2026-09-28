package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.ReportService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GetOutstandingRentTool implements AiTool {
    private final ReportService reportService;

    public GetOutstandingRentTool(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public String name() {
        return "get_outstanding_rent";
    }

    @Override
    public String description() {
        return "Retrieve the outstanding rent summary across all tenants.";
    }

    @Override
    public List<String> requiredParameters() {
        return List.of();
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);
            if (ctx.getRole() == Role.TENANT) {
                return new AiToolResult(null, name(), "Tenants are not allowed to view outstanding rent summaries.", false);
            }

            var dto = reportService.getOutstandingRent();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Total Outstanding Rent: %s%n", dto.getTotalOutstanding()));
            sb.append(String.format("Tenants with Outstanding: %d%n", dto.getTotalTenants()));
            sb.append("Breakdown:\n");
            for (var t : dto.getTenants()) {
                sb.append(String.format("- %s | Property: %s | Unit: %s | Amount: %s | Overdue Days: %d%n",
                        t.tenantName() != null ? t.tenantName() : "N/A",
                        t.propertyName() != null ? t.propertyName() : "N/A",
                        t.unitNumber() != null ? t.unitNumber() : "N/A",
                        t.outstandingAmount() != null ? t.outstandingAmount() : BigDecimal.ZERO,
                        t.overdueDays()
                ));
            }

            return new AiToolResult(null, name(), sb.toString(), true);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve outstanding rent.", false);
        }
    }
}
