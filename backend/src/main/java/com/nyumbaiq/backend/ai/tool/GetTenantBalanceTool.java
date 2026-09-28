package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.entity.Tenant;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.InvoiceService;
import com.nyumbaiq.backend.service.PaymentService;
import com.nyumbaiq.backend.service.TenantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GetTenantBalanceTool implements AiTool {
    private final TenantService tenantService;
    private final InvoiceService invoiceService;
    private final PaymentService paymentService;

    public GetTenantBalanceTool(TenantService tenantService, InvoiceService invoiceService, PaymentService paymentService) {
        this.tenantService = tenantService;
        this.invoiceService = invoiceService;
        this.paymentService = paymentService;
    }

    @Override
    public String name() {
        return "get_tenant_balance";
    }

    @Override
    public String description() {
        return "Retrieve the current balance and payment summary for a tenant.";
    }

    @Override
    public List<String> requiredParameters() {
        return List.of();
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);
            UUID targetTenantId = extractTenantId(parameters, ctx);

            com.nyumbaiq.backend.dto.TenantDto tenant = tenantService.getTenant(targetTenantId);

            BigDecimal totalInvoiced = BigDecimal.ZERO;
            BigDecimal totalPaid = BigDecimal.ZERO;
            BigDecimal balance = BigDecimal.ZERO;

            Pageable pageable = org.springframework.data.domain.Pageable.unpaged();
            var invoices = invoiceService.getAllInvoices(pageable).getContent();
            var payments = paymentService.getAllPayments(pageable).getContent();

            for (var invoice : invoices) {
                if (invoice.getTenant() != null && invoice.getTenant().getId().equals(targetTenantId)) {
                    totalInvoiced = totalInvoiced.add(invoice.getAmount());
                    balance = balance.add(invoice.getBalance() != null ? invoice.getBalance() : BigDecimal.ZERO);
                }
            }

            for (var payment : payments) {
                if (payment.getTenant() != null && payment.getTenant().getId().equals(targetTenantId)
                        && payment.getStatus() == com.nyumbaiq.backend.domain.enums.PaymentStatus.SUCCESS) {
                    totalPaid = totalPaid.add(payment.getAmount());
                }
            }

            String result = String.format(
                    "Tenant: %s%nTotal Invoiced: %s%nTotal Paid: %s%nOutstanding Balance: %s",
                    tenant.getFullName() != null ? tenant.getFullName() : "Unknown",
                    totalInvoiced,
                    totalPaid,
                    balance
            );

            return new AiToolResult(null, name(), result, true);
        } catch (AccessDeniedException | NotFoundException e) {
            return new AiToolResult(null, name(), "Access denied or tenant not found.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve tenant balance.", false);
        }
    }

    private UUID extractTenantId(Map<String, Object> parameters, AiToolContext ctx) {
        if (ctx.getRole() == Role.TENANT) {
            return ctx.getUserId();
        }
        Object param = parameters.get("tenantId");
        if (param instanceof String s) {
            return UUID.fromString(s);
        }
        if (param instanceof UUID uuid) {
            return uuid;
        }
        return ctx.getUserId();
    }
}
