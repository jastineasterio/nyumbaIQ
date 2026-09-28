package com.nyumbaiq.backend.ai;

import com.nyumbaiq.backend.ai.dto.AiChatRequest;
import com.nyumbaiq.backend.ai.dto.AiChatResponse;
import com.nyumbaiq.backend.ai.dto.AiMessage;
import com.nyumbaiq.backend.ai.dto.AiToolResult;
import com.nyumbaiq.backend.security.CurrentUser;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AiService {
    private final AiProvider aiProvider;
    private final AiToolRegistry toolRegistry;
    private final AiConversationService conversationService;
    private final CurrentUser currentUser;

    public AiService(AiProvider aiProvider, AiToolRegistry toolRegistry,
                     AiConversationService conversationService, CurrentUser currentUser) {
        this.aiProvider = aiProvider;
        this.toolRegistry = toolRegistry;
        this.conversationService = conversationService;
        this.currentUser = currentUser;
    }

    public AiChatResponse chat(AiChatRequest request) {
        String conversationId = request.conversationId() != null ? request.conversationId() : UUID.randomUUID().toString();
        List<AiMessage> history = conversationService.getHistory(conversationId);
        if (history.isEmpty() && request.history() != null) {
            history = new ArrayList<>(request.history());
        }

        AiToolContext context = new AiToolContext(currentUser);
        String roleInstruction = buildRoleInstruction(context);

        List<AiMessage> messages = new ArrayList<>();
        messages.add(new AiMessage("user", roleInstruction + "\n\nUser: " + request.message(), new ArrayList<>()));
        messages.addAll(history);

        AiChatResponse response = aiProvider.chat(new AiChatRequest(conversationId, request.message(), messages));

        List<AiToolResult> toolResults = new ArrayList<>();
        if (response.toolResults() != null) {
            toolResults = new ArrayList<>(response.toolResults());
            for (AiToolResult toolResult : toolResults) {
                if (!toolResult.success()) {
                    continue;
                }
                AiTool tool = toolRegistry.get(toolResult.toolName());
                if (tool.requiresConfirmation()) {
                    return new AiChatResponse(
                            conversationId,
                            tool.confirmationMessage(Map.of()),
                            toolResults,
                            true,
                            "Confirm action"
                    );
                }
            }
        }

        conversationService.append(conversationId, new AiMessage("user", request.message(), new ArrayList<>()));
        conversationService.append(conversationId, new AiMessage("assistant", response.reply(), toolResults));

        return new AiChatResponse(conversationId, response.reply(), toolResults, false, null);
    }

    public AiChatResponse confirmAndExecute(String conversationId, String toolName, Map<String, Object> parameters) {
        AiTool tool = toolRegistry.get(toolName);
        AiToolResult result = tool.execute(parameters, currentUser);

        List<AiMessage> history = conversationService.getHistory(conversationId);
        List<AiToolResult> toolResults = List.of(result);
        history.add(new AiMessage("assistant", "Action executed: " + toolName, toolResults));
        for (AiMessage msg : history) {
            conversationService.append(conversationId, msg);
        }

        String reply = result.success()
                ? "Action completed successfully."
                : "Action failed: " + result.result();

        return new AiChatResponse(conversationId, reply, toolResults, false, null);
    }

    public void clearConversation(String conversationId) {
        conversationService.clear(conversationId);
    }

    private String buildRoleInstruction(AiToolContext context) {
        String role = context.getRole().name();
        return switch (role) {
            case "OWNER" -> "You are NyumbaIQ Assistant for an OWNER. You have full access to property-level financials, occupancy, leases, maintenance, and reports. Never expose other owners' data.";
            case "MANAGER" -> "You are NyumbaIQ Assistant for a MANAGER. You can access assigned properties, tenants, leases, billing, payments, maintenance, and reports within your scope. Never access properties you are not assigned to.";
            case "TENANT" -> "You are NyumbaIQ Assistant for a TENANT. You can only access your own lease, payments, statements, receipts, maintenance requests, and access status. Never expose other tenants' data.";
            default -> "You are NyumbaIQ Assistant. Follow role-based access rules.";
        };
    }
}
