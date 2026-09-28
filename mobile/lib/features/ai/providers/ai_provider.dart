import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/network/api_client.dart';
import '../models/ai_models.dart';

class AiProvider extends ChangeNotifier {
  final ApiClient apiClient;
  final List<AiMessage> messages = [];
  String? conversationId;
  bool isLoading = false;
  String? error;
  Map<String, dynamic>? pendingTool;

  AiProvider({required this.apiClient});

  Future<void> sendMessage(String text) async {
    if (text.trim().isEmpty || isLoading) return;
    isLoading = true;
    error = null;
    notifyListeners();

    try {
      final response = await apiClient.post('/ai/chat', body: {
        'conversationId': conversationId,
        'message': text,
        'history': messages.map((e) => e.toJson()).toList(),
      });

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body) as Map<String, dynamic>;
        final chatResponse = AiChatResponse.fromJson(data);
        conversationId = chatResponse.conversationId;
        messages.add(AiMessage(role: 'user', content: text));
        messages.add(AiMessage(
          role: 'assistant',
          content: chatResponse.reply,
          toolResults: chatResponse.toolResults ?? [],
        ));
        if (chatResponse.requiresConfirmation &&
            chatResponse.toolResults != null &&
            chatResponse.toolResults!.isNotEmpty) {
          pendingTool = {
            'conversationId': conversationId,
            'toolName': chatResponse.toolResults!.first.toolName,
          };
        }
      } else {
        error = 'Failed to send message';
      }
    } catch (e) {
      error = 'Error: $e';
    } finally {
      isLoading = false;
      notifyListeners();
    }
  }

  Future<void> confirmTool() async {
    if (pendingTool == null) return;
    final toolName = pendingTool!['toolName'] as String;
    final cid = pendingTool!['conversationId'] as String? ?? conversationId;
    isLoading = true;
    error = null;
    notifyListeners();

    try {
      final response = await apiClient.post('/ai/tools/execute', body: {
        'conversationId': cid,
        'toolName': toolName,
        'parameters': const <String, dynamic>{},
      });

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body) as Map<String, dynamic>;
        final chatResponse = AiChatResponse.fromJson(data);
        messages.add(AiMessage(
          role: 'assistant',
          content: chatResponse.reply,
          toolResults: chatResponse.toolResults ?? [],
        ));
        pendingTool = null;
      } else {
        error = 'Failed to execute action';
        pendingTool = null;
      }
    } catch (e) {
      error = 'Error: $e';
      pendingTool = null;
    } finally {
      isLoading = false;
      notifyListeners();
    }
  }

  Future<void> clearConversation() async {
    if (conversationId == null) return;
    try {
      await apiClient.delete('/ai/conversations/$conversationId');
    } catch (_) {}
    messages.clear();
    conversationId = null;
    pendingTool = null;
    notifyListeners();
  }
}
