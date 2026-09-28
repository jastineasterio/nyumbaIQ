package com.nyumbaiq.backend.ai;

import com.nyumbaiq.backend.ai.dto.AiChatRequest;
import com.nyumbaiq.backend.ai.dto.AiChatResponse;

public interface AiProvider {
    AiChatResponse chat(AiChatRequest request);
}
