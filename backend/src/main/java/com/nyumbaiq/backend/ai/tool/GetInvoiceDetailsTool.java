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
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GetInvoiceDetailsTool implements AiTool {
    private final InvoiceService invoiceService;

    public GetInvoiceDetailsTool(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    public String name() {
        return "get_invoice_details";
    }

    @Override
    public String description() {
        return "Retrieve detailed information about a specific invoice.";
    }

    @Override
    public List<String> requiredParameters() {
        return List.of();
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);
            UUID invoiceId = extractInvoiceId(parameters);
            if (invoiceId == null) {
                return new AiToolResult(null, name(), "Invoice ID is required.", false);
            }

            var invoice = invoiceService.getInvoice(invoiceId);

            if (ctx.getRole() == Role.TENANT && invoice.getTenant() != null && !invoice.getTenant().getId().equals(ctx.getUserId())) {
                return new AiToolResult(null, name(), "Access denied. You can only view your own invoices.", false);
            }

            String result = String.format(
                    "Invoice Details:%nNumber: %s%nTenant: %s%nProperty: %s%nUnit: %s%nLease: %s%nAmount: %s%nAllocated: %s%nBalance: %s%nStatus: %s%nDue: %s",
                    invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : "N/A",
                    invoice.getTenant() != null && invoice.getTenant().getFirstName() != null ? invoice.getTenant().getFirstName() + " " + invoice.getTenant().getLastName() : "N/A",
                    invoice.getProperty() != null && invoice.getProperty().getName() != null ? invoice.getProperty().getName() : "N/A",
                    invoice.getUnit() != null && invoice.getUnit().getUnitNumber() != null ? invoice.getUnit().getUnitNumber() : "N/A",
                    invoice.getLeaseNumber() != null ? invoice.getLeaseNumber() : "N/A",
                    invoice.getAmount() != null ? invoice.getAmount() : "N/A",
                    invoice.getAmountPaid() != null ? invoice.getAmountPaid() : BigDecimal.ZERO,
                    invoice.getBalance() != null ? invoice.getBalance() : BigDecimal.ZERO,
                    invoice.getStatus() != null ? invoice.getStatus() : "N/A",
                    invoice.getDueDate() != null ? invoice.getDueDate().toString() : "N/A"
            );

            return new AiToolResult(null, name(), result, true);
        } catch (NotFoundException e) {
            return new AiToolResult(null, name(), "Invoice not found.", false);
        } catch (AccessDeniedException e) {
            return new AiToolResult(null, name(), "Access denied.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve invoice details.", false);
        }
    }

    private UUID extractInvoiceId(Map<String, Object> parameters) {
        Object param = parameters.get("invoiceId");
        if (param instanceof String s) {
            return UUID.fromString(s);
        }
        if (param instanceof UUID uuid) {
            return uuid;
        }
        return null;
    }
}
