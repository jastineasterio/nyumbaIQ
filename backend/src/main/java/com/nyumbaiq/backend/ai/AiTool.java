package com.nyumbaiq.backend.ai;

import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.security.CurrentUser;

import java.util.Map;

public interface AiTool {
    String name();

    String description();

    java.util.List<String> requiredParameters();

    AiToolResult execute(Map<String, Object> parameters, CurrentUser currentUser);

    default boolean requiresConfirmation() {
        return false;
    }

    default String confirmationMessage(Map<String, Object> parameters) {
        return null;
    }
}
