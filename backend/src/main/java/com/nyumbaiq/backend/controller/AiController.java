package com.nyumbaiq.backend.ai;

import com.nyumbaiq.backend.ai.dto.AiChatRequest;
import com.nyumbaiq.backend.ai.dto.AiChatResponse;
import com.nyumbaiq.backend.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ai")
public class AiController {
    private final AiService aiService;
    private final CurrentUser currentUser;

    public AiController(AiService aiService, CurrentUser currentUser) {
        this.aiService = aiService;
        this.currentUser = currentUser;
    }

    @PostMapping("/chat")
    public ResponseEntity<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        AiChatResponse response = aiService.chat(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tools/execute")
    public ResponseEntity<AiChatResponse> executeTool(@RequestBody Map<String, Object> payload) {
        String conversationId = (String) payload.get("conversationId");
        String toolName = (String) payload.get("toolName");
        @SuppressWarnings("unchecked")
        Map<String, Object> parameters = (Map<String, Object>) payload.getOrDefault("parameters", Map.of());
        AiChatResponse response = aiService.confirmAndExecute(conversationId, toolName, parameters);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<Void> clearConversation(@PathVariable String conversationId) {
        aiService.clearConversation(conversationId);
        return ResponseEntity.noContent().build();
    }
}
