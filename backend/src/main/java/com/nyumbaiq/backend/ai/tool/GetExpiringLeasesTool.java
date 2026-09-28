package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.LeaseStatus;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.LeaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class GetExpiringLeasesTool implements AiTool {
    private final LeaseService leaseService;

    public GetExpiringLeasesTool(LeaseService leaseService) {
        this.leaseService = leaseService;
    }

    @Override
    public String name() {
        return "get_expiring_leases";
    }

    @Override
    public String description() {
        return "Retrieve leases that are expiring within the next 90 days.";
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
                return new AiToolResult(null, name(), "Tenants are not allowed to view expiring leases.", false);
            }

            Page<com.nyumbaiq.backend.dto.LeaseDto> leases = leaseService.getAllLeases(
                    PageRequest.of(0, 50)
            );

            LocalDate threshold = LocalDate.now().plusDays(90);

            StringBuilder sb = new StringBuilder();
            sb.append("Expiring Leases (within 90 days):\n");
            int count = 0;
            for (var l : leases.getContent()) {
                if ((l.getStatus() == LeaseStatus.ACTIVE || l.getStatus() == LeaseStatus.EXPIRING)
                        && l.getEndDate() != null && l.getEndDate().toLocalDate().isBefore(threshold)) {
                    sb.append(String.format("- %s | Tenant: %s | Unit: %s | Property: %s | Ends: %s%n",
                            l.getLeaseNumber() != null ? l.getLeaseNumber() : "N/A",
                            l.getTenant() != null && l.getTenant().getFirstName() != null ? l.getTenant().getFirstName() + " " + l.getTenant().getLastName() : "N/A",
                            l.getUnit() != null && l.getUnit().getUnitNumber() != null ? l.getUnit().getUnitNumber() : "N/A",
                            l.getProperty() != null && l.getProperty().getName() != null ? l.getProperty().getName() : "N/A",
                            l.getEndDate() != null ? l.getEndDate().toString() : "N/A"
                    ));
                    count++;
                }
            }

            if (count == 0) {
                sb.append("No leases expiring within 90 days.");
            }

            return new AiToolResult(null, name(), sb.toString(), true);
        } catch (AccessDeniedException | NotFoundException e) {
            return new AiToolResult(null, name(), "Access denied or resource not found.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve expiring leases.", false);
        }
    }
}
