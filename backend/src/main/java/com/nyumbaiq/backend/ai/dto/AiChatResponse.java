package com.nyumbaiq.backend.ai.dto;

import java.util.List;

public record AiChatResponse(
        String conversationId,
        String reply,
        List<AiToolResult> toolResults,
        boolean requiresConfirmation,
        String confirmationHint
) {
}
