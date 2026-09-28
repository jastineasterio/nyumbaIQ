package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.ReportService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class GetExpenseSummaryTool implements AiTool {
    private final ReportService reportService;

    public GetExpenseSummaryTool(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public String name() {
        return "get_expense_summary";
    }

    @Override
    public String description() {
        return "Retrieve an expense summary for a specified date range.";
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
                return new AiToolResult(null, name(), "Tenants are not allowed to view expense summaries.", false);
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(1);

            var dto = reportService.getExpenseSummary(startDate, endDate);

            String result = String.format(
                    "Expense Summary (%s to %s):%nTotal Expenses: %s%nApproved: %s%nPending: %s%nPaid: %s%nBy Category: %s",
                    startDate,
                    endDate,
                    dto.getTotalExpenses(),
                    dto.getApprovedExpenses(),
                    dto.getPendingExpenses(),
                    dto.getPaidExpenses(),
                    dto.getExpensesByCategory() != null ? dto.getExpensesByCategory().toString() : "N/A"
            );

            return new AiToolResult(null, name(), result, true);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve expense summary.", false);
        }
    }
}
