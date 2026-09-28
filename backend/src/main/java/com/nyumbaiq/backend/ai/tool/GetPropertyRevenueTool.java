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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GetPropertyRevenueTool implements AiTool {
    private final ReportService reportService;

    public GetPropertyRevenueTool(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public String name() {
        return "get_property_revenue";
    }

    @Override
    public String description() {
        return "Retrieve the revenue report for a specific property over a date range.";
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
                return new AiToolResult(null, name(), "Tenants are not allowed to view property revenue reports.", false);
            }

            UUID propertyId = extractPropertyId(parameters);
            if (propertyId == null) {
                return new AiToolResult(null, name(), "Property ID is required.", false);
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(1);

            var dto = reportService.getPropertyRevenueReport(propertyId, startDate, endDate);

            String result = String.format(
                    "Property Revenue Report (%s to %s):%nTotal Income: %s%nTotal Expenses: %s%nNet Profit: %s%nOutstanding Rent: %s",
                    startDate,
                    endDate,
                    dto.getTotalIncome(),
                    dto.getTotalExpenses(),
                    dto.getNetProfit(),
                    dto.getOutstandingRent()
            );

            return new AiToolResult(null, name(), result, true);
        } catch (NotFoundException e) {
            return new AiToolResult(null, name(), "Property not found.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve property revenue.", false);
        }
    }

    private UUID extractPropertyId(Map<String, Object> parameters) {
        Object param = parameters.get("propertyId");
        if (param instanceof String s) {
            return UUID.fromString(s);
        }
        if (param instanceof UUID uuid) {
            return uuid;
        }
        return null;
    }
}
