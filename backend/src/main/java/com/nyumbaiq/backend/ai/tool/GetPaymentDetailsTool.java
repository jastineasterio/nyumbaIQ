package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.PaymentStatus;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.PaymentService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GetPaymentDetailsTool implements AiTool {
    private final PaymentService paymentService;

    public GetPaymentDetailsTool(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public String name() {
        return "get_payment_details";
    }

    @Override
    public String description() {
        return "Retrieve detailed information about a specific payment.";
    }

    @Override
    public List<String> requiredParameters() {
        return List.of();
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);
            UUID paymentId = extractPaymentId(parameters);
            if (paymentId == null) {
                return new AiToolResult(null, name(), "Payment ID is required.", false);
            }

            var payment = paymentService.getPayment(paymentId);

            if (ctx.getRole() == Role.TENANT && payment.getTenant() != null && !payment.getTenant().getId().equals(ctx.getUserId())) {
                return new AiToolResult(null, name(), "Access denied. You can only view your own payments.", false);
            }

            String result = String.format(
                    "Payment Details:%nReference: %s%nTenant: %s%nProperty: %s%nAmount: %s%nMethod: %s%nProvider: %s%nStatus: %s%nInitiated: %s%nConfirmed: %s",
                    payment.getPaymentReference() != null ? payment.getPaymentReference() : "N/A",
                    payment.getTenant() != null && payment.getTenant().getFirstName() != null ? payment.getTenant().getFirstName() + " " + payment.getTenant().getLastName() : "N/A",
                    payment.getProperty() != null && payment.getProperty().getName() != null ? payment.getProperty().getName() : "N/A",
                    payment.getAmount() != null ? payment.getAmount() : "N/A",
                    payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "N/A",
                    payment.getProvider() != null ? payment.getProvider() : "N/A",
                    payment.getStatus() != null ? payment.getStatus() : "N/A",
                    payment.getInitiatedAt() != null ? payment.getInitiatedAt().toString() : "N/A",
                    payment.getConfirmedAt() != null ? payment.getConfirmedAt().toString() : "N/A"
            );

            return new AiToolResult(null, name(), result, true);
        } catch (NotFoundException e) {
            return new AiToolResult(null, name(), "Payment not found.", false);
        } catch (AccessDeniedException e) {
            return new AiToolResult(null, name(), "Access denied.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve payment details.", false);
        }
    }

    private UUID extractPaymentId(Map<String, Object> parameters) {
        Object param = parameters.get("paymentId");
        if (param instanceof String s) {
            return UUID.fromString(s);
        }
        if (param instanceof UUID uuid) {
            return uuid;
        }
        return null;
    }
}
