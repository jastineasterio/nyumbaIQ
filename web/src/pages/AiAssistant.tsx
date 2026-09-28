import { useState, useEffect, useRef } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { aiApi } from '../api/ai';
import type { AiChatRequest, AiChatResponse, AiMessage } from '../types/ai';
import { useAuth } from '../hooks/useAuth';
import { useRole } from '../hooks/useRole';
import toast from 'react-hot-toast';

const ROLE_SUGGESTIONS: Record<string, string[]> = {
  OWNER: [
    'How much rent did my properties generate this month?',
    'Which units are vacant?',
    'Which leases expire within 30 days?',
    'Show outstanding rent.',
    'Show property revenue.',
    'Which properties have the highest outstanding balances?',
    'Show recent maintenance requests.',
  ],
  MANAGER: [
    'Which tenants have overdue rent?',
    'Show unpaid invoices.',
    'Which units are vacant?',
    'Which leases are expiring?',
    'Show maintenance requests assigned to me.',
    "Show today's collections.",
  ],
  TENANT: [
    'How much rent do I owe?',
    'When is my next rent due?',
    'Show my payment history.',
    'Show my lease information.',
    'Generate my statement.',
    'Report a maintenance problem.',
  ],
};

const AiAssistant = () => {
  const { user, isAuthenticated } = useAuth();
  const { role } = useRole(isAuthenticated ? undefined : 'TENANT');
  const queryClient = useQueryClient();
  const [conversationId, setConversationId] = useState<string | null>(null);
  const [messages, setMessages] = useState<AiMessage[]>([]);
  const [input, setInput] = useState('');
  const [pendingTool, setPendingTool] = useState<{ name: string; parameters?: Record<string, unknown> } | null>(null);
  const [error, setError] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const chatMutation = useMutation({
    mutationFn: async (payload: AiChatRequest) => aiApi.chat(payload),
    onSuccess: (response) => {
      setConversationId(response.conversationId);
      setMessages((prev) => [
        ...prev,
        { role: 'assistant', content: response.reply, toolResults: response.toolResults },
      ]);
      if (response.requiresConfirmation && response.toolResults && response.toolResults.length > 0) {
        setPendingTool({ name: response.toolResults[0].toolName });
      }
      setError(null);
    },
    onError: () => setError('Failed to send message. Please try again.'),
  });

  const executeToolMutation = useMutation({
    mutationFn: async (payload: {
      conversationId: string;
      toolName: string;
      parameters?: Record<string, unknown>;
    }) => aiApi.executeTool(payload),
    onSuccess: (response) => {
      setMessages((prev) => [
        ...prev,
        { role: 'assistant', content: response.reply, toolResults: response.toolResults },
      ]);
      setPendingTool(null);
      toast.success(response.reply);
    },
    onError: () => {
      setError('Failed to execute action.');
      setPendingTool(null);
    },
  });

  const clearMutation = useMutation({
    mutationFn: async (id: string) => aiApi.clearConversation(id),
    onSuccess: () => {
      setMessages([]);
      setConversationId(null);
      setPendingTool(null);
    },
  });

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const sendMessage = (text?: string) => {
    const messageText = text || input.trim();
    if (!messageText || chatMutation.isPending) return;
    setInput('');
    setMessages((prev) => [...prev, { role: 'user', content: messageText }]);
    chatMutation.mutate({ conversationId: conversationId ?? undefined, message: messageText, history: messages });
  };

  const handleConfirm = () => {
    if (!conversationId || !pendingTool) return;
    executeToolMutation.mutate({
      conversationId,
      toolName: pendingTool.name,
      parameters: pendingTool.parameters,
    });
  };

  const handleClear = () => {
    if (conversationId) {
      clearMutation.mutate(conversationId);
    } else {
      setMessages([]);
    }
  };

  const suggestions = ROLE_SUGGESTIONS[role] || ROLE_SUGGESTIONS.TENANT;

  return (
    <div className="flex flex-col h-[calc(100vh-4rem)]">
      <div className="flex items-center justify-between px-4 py-3 border-b border-gray-200 dark:border-gray-700">
        <div>
          <h1 className="text-xl font-semibold text-gray-900 dark:text-white">AI Assistant</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">Ask anything about your properties, tenants, payments, and more.</p>
        </div>
        <button
          onClick={handleClear}
          className="text-sm text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
        >
          Clear
        </button>
      </div>

      <div className="flex-1 overflow-y-auto px-4 py-4 space-y-4">
        {messages.length === 0 && (
          <div className="space-y-3">
            <p className="text-sm text-gray-500 dark:text-gray-400">Suggested questions:</p>
            <div className="flex flex-wrap gap-2">
              {suggestions.map((suggestion) => (
                <button
                  key={suggestion}
                  onClick={() => sendMessage(suggestion)}
                  className="text-sm px-3 py-2 rounded-full border border-gray-200 dark:border-gray-700 text-gray-700 dark:text-gray-200 hover:border-primary-500 dark:hover:border-primary-400"
                >
                  {suggestion}
                </button>
              ))}
            </div>
          </div>
        )}

        {messages.map((message, index) => (
          <div key={index} className={`flex ${message.role === 'user' ? 'justify-end' : 'justify-start'}`}>
            <div
              className={`max-w-[80%] rounded-lg px-4 py-2 text-sm ${
                message.role === 'user'
                  ? 'bg-primary-600 text-white'
                  : 'bg-gray-100 dark:bg-gray-800 text-gray-900 dark:text-gray-100'
              }`}
            >
              <p className="whitespace-pre-wrap">{message.content}</p>
              {message.toolResults && message.toolResults.length > 0 && (
                <div className="mt-2 space-y-1">
                  {message.toolResults.map((tool) => (
                    <div key={tool.toolCallId} className="text-xs opacity-80">
                      Tool: {tool.toolName} — {tool.success ? 'Success' : 'Failed'}
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        ))}

        {chatMutation.isPending && (
          <div className="flex justify-start">
            <div className="bg-gray-100 dark:bg-gray-800 rounded-lg px-4 py-2 text-sm text-gray-500 dark:text-gray-400">
              Thinking...
            </div>
          </div>
        )}

        {error && <div className="text-sm text-red-600 dark:text-red-400">{error}</div>}

        <div ref={messagesEndRef} />
      </div>

      {pendingTool && (
        <div className="px-4 py-3 border-t border-yellow-200 bg-yellow-50 dark:border-yellow-700 dark:bg-yellow-900/20">
          <p className="text-sm text-yellow-800 dark:text-yellow-200">
            The assistant wants to run <span className="font-semibold">{pendingTool.name}</span>. Do you want to continue?
          </p>
          <div className="mt-2 flex gap-2">
            <button
              onClick={handleConfirm}
              disabled={executeToolMutation.isPending}
              className="px-3 py-1.5 text-sm rounded bg-primary-600 text-white hover:bg-primary-700 disabled:opacity-50"
            >
              {executeToolMutation.isPending ? 'Running...' : 'Yes, continue'}
            </button>
            <button
              onClick={() => setPendingTool(null)}
              className="px-3 py-1.5 text-sm rounded border border-gray-300 text-gray-700 hover:bg-gray-50 dark:border-gray-600 dark:text-gray-200 dark:hover:bg-gray-800"
            >
              Cancel
            </button>
          </div>
        </div>
      )}

      <div className="border-t border-gray-200 dark:border-gray-700 px-4 py-3">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            sendMessage();
          }}
          className="flex gap-2"
        >
          <input
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Ask NyumbaIQ Assistant..."
            className="flex-1 rounded-lg border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-900 px-3 py-2 text-sm text-gray-900 dark:text-gray-100 focus:outline-none focus:ring-2 focus:ring-primary-500"
          />
          <button
            type="submit"
            disabled={!input.trim() || chatMutation.isPending}
            className="rounded-lg bg-primary-600 px-4 py-2 text-sm font-medium text-white hover:bg-primary-700 disabled:opacity-50"
          >
            Send
          </button>
        </form>
      </div>
    </div>
  );
};

export default AiAssistant;
