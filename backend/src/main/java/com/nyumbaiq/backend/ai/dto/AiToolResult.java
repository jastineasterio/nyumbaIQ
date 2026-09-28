package com.nyumbaiq.backend.ai.dto;

public record AiToolResult(
        String toolCallId,
        String toolName,
        String result,
        boolean success
) {
}
