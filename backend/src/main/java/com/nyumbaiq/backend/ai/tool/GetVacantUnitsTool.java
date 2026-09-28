package com.nyumbaiq.backend.ai.tool;

import com.nyumbaiq.backend.ai.AiTool;
import com.nyumbaiq.backend.ai.AiToolContext;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.domain.enums.Role;
import com.nyumbaiq.backend.domain.enums.UnitStatus;
import com.nyumbaiq.backend.exception.AccessDeniedException;
import com.nyumbaiq.backend.security.CurrentUser;
import com.nyumbaiq.backend.service.UnitService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class GetVacantUnitsTool implements AiTool {
    private final UnitService unitService;

    public GetVacantUnitsTool(UnitService unitService) {
        this.unitService = unitService;
    }

    @Override
    public String name() {
        return "get_vacant_units";
    }

    @Override
    public String description() {
        return "Retrieve a list of vacant units across all properties.";
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
                return new AiToolResult(null, name(), "Tenants are not allowed to view vacant units.", false);
            }

            Page<com.nyumbaiq.backend.dto.UnitDto> units = unitService.getUnitsByFloor(
                    UUID.randomUUID(),
                    PageRequest.of(0, 100)
            );

            StringBuilder sb = new StringBuilder();
            sb.append("Vacant Units:\n");
            int count = 0;
            for (var u : units.getContent()) {
                if (u.getStatus() == UnitStatus.VACANT) {
                    sb.append(String.format("- Unit: %s | Floor: %s | Building: %s | Property: %s%n",
                            u.getUnitNumber() != null ? u.getUnitNumber() : "N/A",
                            u.getFloor() != null && u.getFloor().getName() != null ? u.getFloor().getName() : "N/A",
                            u.getBuilding() != null && u.getBuilding().getName() != null ? u.getBuilding().getName() : "N/A",
                            u.getProperty() != null && u.getProperty().getName() != null ? u.getProperty().getName() : "N/A"
                    ));
                    count++;
                }
            }

            if (count == 0) {
                sb.append("No vacant units found.");
            }

            return new AiToolResult(null, name(), sb.toString(), true);
        } catch (Exception e) {
            return new AiToolResult(null, name(), "Failed to retrieve vacant units.", false);
        }
    }
}
