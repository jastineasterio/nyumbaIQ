package com.nyumbaiq.backend.ai;

import com.nyumbaiq.backend.ai.dto.AiChatRequest;
import com.nyumbaiq.backend.ai.dto.AiChatResponse;
import com.nyumbaiq.backend.ai.dto.AiMessage;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.*;

@Component
public class GeminiAiProvider implements AiProvider {
    private final GeminiProperties properties;
    private final RestClient restClient;

    public GeminiAiProvider(GeminiProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Override
    public AiChatResponse chat(AiChatRequest request) {
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            return new AiChatResponse(request.conversationId(), "AI is not configured.", Collections.emptyList(), false, null);
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        Map<String, Object> systemInstruction = new LinkedHashMap<>();
        systemInstruction.put("parts", List.of(Map.of("text", "You are NyumbaIQ Assistant. Do not reveal database credentials, secrets, or internal implementation. Do not execute destructive actions without confirmation.")));
        payload.put("system_instruction", systemInstruction);

        Map<String, Object> generationConfig = new LinkedHashMap<>();
        generationConfig.put("temperature", properties.getTemperature());
        generationConfig.put("maxOutputTokens", properties.getMaxOutputTokens());
        payload.put("generationConfig", generationConfig);

        List<Map<String, Object>> contents = new ArrayList<>();
        if (request.history() != null) {
            for (AiMessage message : request.history()) {
                Map<String, Object> part = new LinkedHashMap<>();
                part.put("text", message.content());
                Map<String, Object> content = new LinkedHashMap<>();
                content.put("role", message.role());
                content.put("parts", List.of(part));
                contents.add(content);
            }
        }
        Map<String, Object> userPart = new LinkedHashMap<>();
        userPart.put("text", request.message());
        Map<String, Object> userContent = new LinkedHashMap<>();
        userContent.put("role", "user");
        userContent.put("parts", List.of(userPart));
        contents.add(userContent);
        payload.put("contents", contents);

        List<Map<String, Object>> toolDefinitions = new ArrayList<>();
        for (Map<String, Object> def : List.copyOf(toolDefinitions)) {
            toolDefinitions.add(def);
        }
        payload.put("tools", List.of(Map.of("function_declarations", toolDefinitions)));

        try {
            Map<String, Object> response = restClient.post()
                    .uri("/models/" + properties.getModel() + ":generateContent?key=" + properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, resp) -> {
                        throw new RestClientResponseException(resp.getStatusText(), resp.getStatusCode().value(), resp.getStatusCode().toString(), resp.getHeaders(), null, null);
                    })
                    .body(Map.class);

            String reply = extractText(response);
            List<AiToolResult> toolResults = new ArrayList<>();
            boolean requiresConfirmation = false;
            String confirmationHint = null;

            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> candidate = candidates.get(0);
                Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                if (parts != null) {
                    for (Map<String, Object> part : parts) {
                        if (part.containsKey("functionCall")) {
                            Map<String, Object> functionCall = (Map<String, Object>) part.get("functionCall");
                            String toolName = (String) functionCall.get("name");
                            Map<String, Object> args = (Map<String, Object>) functionCall.get("args");
                            toolResults.add(new AiToolResult(UUID.randomUUID().toString(), toolName, "Pending execution", true));
                            requiresConfirmation = true;
                            confirmationHint = "Tool call: " + toolName;
                        } else if (part.containsKey("text")) {
                            reply = (String) part.get("text");
                        }
                    }
                }
            }

            return new AiChatResponse(request.conversationId(), reply, toolResults, requiresConfirmation, confirmationHint);
        } catch (RestClientResponseException ex) {
            return new AiChatResponse(request.conversationId(), "AI service error: " + ex.getStatusText(), Collections.emptyList(), false, null);
        } catch (Exception ex) {
            return new AiChatResponse(request.conversationId(), "AI service unavailable.", Collections.emptyList(), false, null);
        }
    }

    private String extractText(Map<String, Object> response) {
        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            return "No response.";
        }
        Map<String, Object> candidate = candidates.get(0);
        Map<String, Object> content = (Map<String, Object>) candidate.get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        if (parts == null || parts.isEmpty()) {
            return "No response.";
        }
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> part : parts) {
            if (part.containsKey("text")) {
                sb.append((String) part.get("text"));
            }
        }
        return sb.toString().trim();
    }
}
