package com.nyumbaiq.backend.ai.dto;

public record AiChatRequest(
        String conversationId,
        String message,
        java.util.List<AiMessage> history
) {
}
