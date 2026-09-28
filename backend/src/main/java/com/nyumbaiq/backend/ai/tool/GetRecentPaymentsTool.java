package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.PaymentStatus;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GetRecentPaymentsTool implements AiTool {
    private final PaymentService paymentService;

    public GetRecentPaymentsTool(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    public String name() {
        return "get_recent_payments";
    }

    @Override
    public String description() {
        return "Retrieve the most recent payments across the system.";
    }

    @Override
    public List<String> requiredParameters() {
        return List.of();
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);
            Page<com.nyumbaiq.backend.dto.PaymentDto> payments = paymentService.getAllPayments(
                    PageRequest.of(0, 10, Sort.by("createdAt").descending())
            );

            StringBuilder sb = new StringBuilder();
            sb.append("Recent Payments:\n");
            int count = 0;
            for (var p : payments.getContent()) {
                if (ctx.getRole() == Role.TENANT && p.getTenant() != null && !p.getTenant().getId().equals(ctx.getUserId())) {
                    continue;
                }
                sb.append(String.format("- %s | %s | %s | Status: %s | Date: %s%n",
                        p.getPaymentReference() != null ? p.getPaymentReference() : "N/A",
                        p.getTenant() != null && p.getTenant().getFirstName() != null ? p.getTenant().getFirstName() + " " + p.getTenant().getLastName() : "N/A",
                        p.getAmount() != null ? p.getAmount() : "N/A",
                        p.getStatus() != null ? p.getStatus().name() : "N/A",
                        p.getInitiatedAt() != null ? p.getInitiatedAt().toString() : "N/A"
                ));
                count++;
            }

            if (count == 0) {
                sb.append("No recent payments found.");
            }

            return new AiToolResult(null, name(), sb.toString(), true);
        } catch (AccessDeniedException e) {
            return new AiToolResult(null, name(), "Access denied.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve recent payments.", false);
        }
    }
}
