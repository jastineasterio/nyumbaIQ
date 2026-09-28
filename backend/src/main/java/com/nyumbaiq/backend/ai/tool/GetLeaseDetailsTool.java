package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.LeaseService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GetLeaseDetailsTool implements AiTool {
    private final LeaseService leaseService;

    public GetLeaseDetailsTool(LeaseService leaseService) {
        this.leaseService = leaseService;
    }

    @Override
    public String name() {
        return "get_lease_details";
    }

    @Override
    public String description() {
        return "Retrieve detailed information about a specific lease.";
    }

    @Override
    public List<String> requiredParameters() {
        return List.of();
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);
            UUID leaseId = extractLeaseId(parameters);
            if (leaseId == null) {
                return new AiToolResult(null, name(), "Lease ID is required.", false);
            }

            var lease = leaseService.getLease(leaseId);

            if (ctx.getRole() == Role.TENANT && lease.getTenant() != null && !lease.getTenant().getId().equals(ctx.getUserId())) {
                return new AiToolResult(null, name(), "Access denied. You can only view your own lease.", false);
            }

            String result = String.format(
                    "Lease Details:%nNumber: %s%nTenant: %s%nProperty: %s%nUnit: %s%nStart: %s%nEnd: %s%nRent: %s%nStatus: %s",
                    lease.getLeaseNumber() != null ? lease.getLeaseNumber() : "N/A",
                    lease.getTenant() != null && lease.getTenant().getFirstName() != null ? lease.getTenant().getFirstName() + " " + lease.getTenant().getLastName() : "N/A",
                    lease.getProperty() != null && lease.getProperty().getName() != null ? lease.getProperty().getName() : "N/A",
                    lease.getUnit() != null && lease.getUnit().getUnitNumber() != null ? lease.getUnit().getUnitNumber() : "N/A",
                    lease.getStartDate() != null ? lease.getStartDate().toString() : "N/A",
                    lease.getEndDate() != null ? lease.getEndDate().toString() : "N/A",
                    lease.getRentAmount() != null ? lease.getRentAmount() : "N/A",
                    lease.getStatus() != null ? lease.getStatus() : "N/A"
            );

            return new AiToolResult(null, name(), result, true);
        } catch (NotFoundException e) {
            return new AiToolResult(null, name(), "Lease not found.", false);
        } catch (AccessDeniedException e) {
            return new AiToolResult(null, name(), "Access denied.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve lease details.", false);
        }
    }

    private UUID extractLeaseId(Map<String, Object> parameters) {
        Object param = parameters.get("leaseId");
        if (param instanceof String s) {
            return UUID.fromString(s);
        }
        if (param instanceof UUID uuid) {
            return uuid;
        }
        return null;
    }
}
