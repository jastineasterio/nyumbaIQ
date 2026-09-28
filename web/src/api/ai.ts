import { apiClient } from './client';
import type { AiChatRequest, AiChatResponse } from '../types/ai';

export const aiApi = {
  chat: async (payload: AiChatRequest): Promise<AiChatResponse> => {
    const { data } = await apiClient.post<AiChatResponse>('/ai/chat', payload);
    return data;
  },

  executeTool: async (payload: {
    conversationId: string;
    toolName: string;
    parameters?: Record<string, unknown>;
  }): Promise<AiChatResponse> => {
    const { data } = await apiClient.post<AiChatResponse>('/ai/tools/execute', payload);
    return data;
  },

  clearConversation: async (conversationId: string): Promise<void> => {
    await apiClient.delete(`/ai/conversations/${conversationId}`);
  },
};
