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
public class GetFinanceSummaryTool implements AiTool {
    private final ReportService reportService;

    public GetFinanceSummaryTool(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public String name() {
        return "get_finance_summary";
    }

    @Override
    public String description() {
        return "Retrieve a complete financial summary including income and expenses.";
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
                return new AiToolResult(null, name(), "Tenants are not allowed to view financial summaries.", false);
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusMonths(1);

            var income = reportService.getIncomeSummary(startDate, endDate);
            var expense = reportService.getExpenseSummary(startDate, endDate);

            BigDecimal netCashFlow = income.getTotalIncome().subtract(expense.getTotalExpenses());

            String result = String.format(
                    "Financial Summary (%s to %s):%nTotal Income: %s%nTotal Expenses: %s%nNet Cash Flow: %s%nCollection Rate: %s%%%nExpected Income: %s",
                    startDate,
                    endDate,
                    income.getTotalIncome(),
                    expense.getTotalExpenses(),
                    netCashFlow,
                    income.getCollectionRate(),
                    income.getExpectedIncome()
            );

            return new AiToolResult(null, name(), result, true);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve financial summary.", false);
        }
    }
}
