package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.PaymentService;
import com.nyumbaiq.backend.service.TenantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GetPaymentHistoryTool implements AiTool {
    private final PaymentService paymentService;
    private final TenantService tenantService;

    public GetPaymentHistoryTool(PaymentService paymentService, TenantService tenantService) {
        this.paymentService = paymentService;
        this.tenantService = tenantService;
    }

    @Override
    public String name() {
        return "get_payment_history";
    }

    @Override
    public String description() {
        return "Retrieve the payment history for a tenant.";
    }

    @Override
    public List<String> requiredParameters() {
        return List.of();
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);
            UUID targetTenantId = resolveTenantId(parameters, ctx);

            Page<com.nyumbaiq.backend.dto.PaymentDto> payments = paymentService.getAllPayments(
                    PageRequest.of(0, 20, Sort.by("createdAt").descending())
            );

            StringBuilder sb = new StringBuilder();
            sb.append("Payment History:\n");
            int count = 0;
            for (var p : payments.getContent()) {
                if (targetTenantId != null && p.getTenant() != null && !p.getTenant().getId().equals(targetTenantId)) {
                    continue;
                }
                sb.append(String.format("- %s | %s | %s | %s | Status: %s%n",
                        p.getPaymentReference() != null ? p.getPaymentReference() : "N/A",
                        p.getAmount() != null ? p.getAmount() : "N/A",
                        p.getPaymentMethod() != null ? p.getPaymentMethod() : "N/A",
                        p.getInitiatedAt() != null ? p.getInitiatedAt().toString() : "N/A",
                        p.getStatus() != null ? p.getStatus().name() : "N/A"
                ));
                count++;
            }

            if (count == 0) {
                sb.append("No payments found.");
            }

            return new AiToolResult(null, name(), sb.toString(), true);
        } catch (AccessDeniedException | NotFoundException e) {
            return new AiToolResult(null, name(), "Access denied or resource not found.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve payment history.", false);
        }
    }

    private UUID resolveTenantId(Map<String, Object> parameters, AiToolContext ctx) {
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
        return null;
    }
}
