package com.nyumbaiq.backend.ai.dto;

public record AiMessage(
        String role,
        String content,
        java.util.List<AiToolResult> toolResults
) {
}
