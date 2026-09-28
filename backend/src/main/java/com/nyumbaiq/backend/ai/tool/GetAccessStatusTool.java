package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.exception.NotFoundException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.SmartLockService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GetAccessStatusTool implements AiTool {
    private final SmartLockService smartLockService;

    public GetAccessStatusTool(SmartLockService smartLockService) {
        this.smartLockService = smartLockService;
    }

    @Override
    public String name() {
        return "get_access_status";
    }

    @Override
    public String description() {
        return "Retrieve the access status for smart locks assigned to a tenant.";
    }

    @Override
    public List<String> requiredParameters() {
        return List.of();
    }

    @Override
    public AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser) {
        try {
            AiToolContext ctx = new AiToolContext(currentUser);

            Page<com.nyumbaiq.backend.dto.SmartLockDto> locks = smartLockService.getAllSmartLocks(
                    PageRequest.of(0, 20)
            );

            StringBuilder sb = new StringBuilder();
            sb.append("Smart Lock Access Status:\n");
            int count = 0;
            for (var lock : locks.getContent()) {
                sb.append(String.format("- %s | Unit: %s | Status: %s | Last Update: %s%n",
                        lock.getName() != null ? lock.getName() : "N/A",
                        lock.getUnitNumber() != null ? lock.getUnitNumber() : "N/A",
                        lock.getStatus() != null ? lock.getStatus() : "N/A",
                        lock.getLastStatusUpdate() != null ? lock.getLastStatusUpdate().toString() : "N/A"
                ));
                count++;
            }

            if (count == 0) {
                sb.append("No smart locks found.");
            }

            return new AiToolResult(null, name(), sb.toString(), true);
        } catch (AccessDeniedException | NotFoundException e) {
            return new AiToolResult(null, name(), "Access denied or resource not found.", false);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve access status.", false);
        }
    }
}
