class AiMessage {
  final String role;
  final String content;
  final List<AiToolResult> toolResults;

  AiMessage({required this.role, required this.content, this.toolResults = const []});

  factory AiMessage.fromJson(Map<String, dynamic> json) {
    return AiMessage(
      role: json['role'] as String? ?? 'user',
      content: json['content'] as String? ?? '',
      toolResults: (json['toolResults'] as List<dynamic>? ?? [])
          .map((e) => AiToolResult.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() => {
        'role': role,
        'content': content,
        'toolResults': toolResults.map((e) => e.toJson()).toList(),
      };
}

class AiToolResult {
  final String toolCallId;
  final String toolName;
  final String result;
  final bool success;

  AiToolResult({
    required this.toolCallId,
    required this.toolName,
    required this.result,
    required this.success,
  });

  factory AiToolResult.fromJson(Map<String, dynamic> json) {
    return AiToolResult(
      toolCallId: json['toolCallId'] as String? ?? '',
      toolName: json['toolName'] as String? ?? '',
      result: json['result'] as String? ?? '',
      success: json['success'] as bool? ?? false,
    );
  }

  Map<String, dynamic> toJson() => {
        'toolCallId': toolCallId,
        'toolName': toolName,
        'result': result,
        'success': success,
      };
}

class AiChatResponse {
  final String conversationId;
  final String reply;
  final List<AiToolResult> toolResults;
  final bool requiresConfirmation;
  final String? confirmationHint;

  AiChatResponse({
    required this.conversationId,
    required this.reply,
    this.toolResults = const [],
    this.requiresConfirmation = false,
    this.confirmationHint,
  });

  factory AiChatResponse.fromJson(Map<String, dynamic> json) {
    return AiChatResponse(
      conversationId: json['conversationId'] as String? ?? '',
      reply: json['reply'] as String? ?? '',
      toolResults: (json['toolResults'] as List<dynamic>? ?? [])
          .map((e) => AiToolResult.fromJson(e as Map<String, dynamic>))
          .toList(),
      requiresConfirmation: json['requiresConfirmation'] as bool? ?? false,
      confirmationHint: json['confirmationHint'] as String?,
    );
  }
}
