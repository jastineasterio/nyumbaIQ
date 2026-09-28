export interface AiChatRequest {
  conversationId?: string;
  message: string;
  history?: AiMessage[];
}

export interface AiMessage {
  role: 'user' | 'assistant' | 'system';
  content: string;
  toolResults?: AiToolResult[];
}

export interface AiToolResult {
  toolCallId: string;
  toolName: string;
  result: string;
  success: boolean;
}

export interface AiChatResponse {
  conversationId: string;
  reply: string;
  toolResults?: AiToolResult[];
  requiresConfirmation: boolean;
  confirmationHint?: string;
}
