package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.PaymentMethod;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.PaymentService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CreatePaymentDraftTool implements AiTool {
    private final PaymentService paymentService;

    public CreatePaymentDraftTool(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public String name() {
        return "create_payment_draft";
    }

    @Override
    public String description() {
        return "Create a payment draft record for a tenant.";
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
        Object amount = parameters.get("amount");
        return "Create payment draft for tenant " + (tenantId != null ? tenantId : "unknown") + " with amount " + (amount != null ? amount : "unknown") + "?";
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);

            UUID tenantId = extractUuid(parameters, "tenantId");
            BigDecimal amount = extractBigDecimal(parameters, "amount");
            UUID propertyId = extractUuid(parameters, "propertyId");

            if (tenantId == null || amount == null || propertyId == null) {
                return new AiToolResult(null, name(), "Tenant ID, amount, and property ID are required.", false);
            }

            if (ctx.getRole() == Role.TENANT && !tenantId.equals(ctx.getUserId())) {
                return new AiToolResult(null, name(), "Tenants can only create payment drafts for themselves.", false);
            }

            com.nyumbaiq.backend.dto.CreatePaymentRequest request =
                    new com.nyumbaiq.backend.dto.CreatePaymentRequest(
                            tenantId,
                            null,
                            propertyId,
                            amount,
                            "TZS",
                            PaymentMethod.CASH,
                            "MANUAL",
                            null,
                            null
                    );

            var payment = paymentService.createPayment(request);

            return new AiToolResult(null, name(), "Payment draft created successfully with reference: " + (payment.getPaymentReference() != null ? payment.getPaymentReference() : "N/A"), true);
        } catch (NotFoundException e) {
            return new AiToolResult(null, name(), "Tenant or property not found.", false);
        } catch (AccessDeniedException e) {
            return new AiToolResult(null, name(), "Access denied.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to create payment draft.", false);
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

    private BigDecimal extractBigDecimal(Map<String, Object> parameters, String key) {
        Object param = parameters.get(key);
        if (param instanceof BigDecimal bd) {
            return bd;
        }
        if (param instanceof String s) {
            return new BigDecimal(s);
        }
        if (param instanceof Number n) {
            return new BigDecimal(n.toString());
        }
        return null;
    }
}
