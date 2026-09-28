package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.InvoiceStatus;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GetOverdueTenantsTool implements AiTool {
    private final InvoiceService invoiceService;

    public GetOverdueTenantsTool(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    public String name() {
        return "get_overdue_tenants";
    }

    @Override
    public String description() {
        return "Retrieve a list of tenants with overdue invoices.";
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
                return new AiToolResult(null, name(), "Tenants are not allowed to view overdue tenant lists.", false);
            }

            Page<com.nyumbaiq.backend.dto.InvoiceDto> invoices = invoiceService.getAllInvoices(
                    PageRequest.of(0, 50)
            );

            StringBuilder sb = new StringBuilder();
            sb.append("Overdue Tenants:\n");
            int count = 0;
            for (var inv : invoices.getContent()) {
                if (inv.getStatus() == InvoiceStatus.OVERDUE) {
                    sb.append(String.format("- %s | Property: %s | Unit: %s | Amount: %s | Balance: %s | Due: %s%n",
                            inv.getTenant() != null && inv.getTenant().getFirstName() != null ? inv.getTenant().getFirstName() + " " + inv.getTenant().getLastName() : "N/A",
                            inv.getProperty() != null && inv.getProperty().getName() != null ? inv.getProperty().getName() : "N/A",
                            inv.getUnit() != null && inv.getUnit().getUnitNumber() != null ? inv.getUnit().getUnitNumber() : "N/A",
                            inv.getAmount() != null ? inv.getAmount() : "N/A",
                            inv.getBalance() != null ? inv.getBalance() : "N/A",
                            inv.getDueDate() != null ? inv.getDueDate().toString() : "N/A"
                    ));
                    count++;
                }
            }

            if (count == 0) {
                sb.append("No overdue tenants found.");
            }

            return new AiToolResult(null, name(), sb.toString(), true);
        } catch (AccessDeniedException | NotFoundException e) {
            return new AiToolResult(null, name(), "Access denied or resource not found.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve overdue tenants.", false);
        }
    }
}
