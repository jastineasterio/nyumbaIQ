import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/ai_provider.dart';
import '../../../core/widgets/loading_indicator.dart';

class AiAssistantScreen extends StatefulWidget {
  const AiAssistantScreen({super.key});

  @override
  State<AiAssistantScreen> createState() => _AiAssistantScreenState();
}

class _AiAssistantScreenState extends State<AiAssistantScreen> {
  final TextEditingController _controller = TextEditingController();

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final aiProvider = context.watch<AiProvider>();
    final messages = aiProvider.messages;

    return Scaffold(
      appBar: AppBar(title: const Text('AI Assistant')),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: messages.length,
              itemBuilder: (context, index) {
                final message = messages[index];
                final isUser = message.role == 'user';
                return Align(
                  alignment: isUser ? Alignment.centerRight : Alignment.centerLeft,
                  child: Container(
                    margin: const EdgeInsets.symmetric(vertical: 4),
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: isUser
                          ? Theme.of(context).primaryColor
                          : Theme.of(context).colorScheme.surfaceContainerHighest,
                      borderRadius: BorderRadius.circular(12),
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(message.content, style: TextStyle(color: isUser ? Colors.white : null)),
                        if (message.toolResults != null && message.toolResults!.isNotEmpty)
                          Padding(
                            padding: const EdgeInsets.only(top: 8.0),
                            child: Text(
                              'Tools: ${message.toolResults!.map((e) => e.toolName).join(', ')}',
                              style: TextStyle(fontSize: 12, color: isUser ? Colors.white70 : null),
                            ),
                          ),
                      ],
                    ),
                  ),
                );
              },
            ),
          ),
          if (aiProvider.pendingTool != null)
            Container(
              padding: const EdgeInsets.all(16),
              color: Colors.amber.shade50,
              child: Row(
                children: [
                  Expanded(
                    child: Text(
                      'Run ${aiProvider.pendingTool!['toolName']}?',
                      style: const TextStyle(fontWeight: FontWeight.w600),
                    ),
                  ),
                  TextButton(
                    onPressed: aiProvider.isLoading ? null : () => aiProvider.confirmTool(),
                    child: const Text('Yes'),
                  ),
                  TextButton(
                    onPressed: () => setState(() => aiProvider.pendingTool = null),
                    child: const Text('Cancel'),
                  ),
                ],
              ),
            ),
          if (aiProvider.error != null)
            Padding(
              padding: const EdgeInsets.all(16.0),
              child: Text(aiProvider.error!, style: const TextStyle(color: Colors.red)),
            ),
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _controller,
                    decoration: const InputDecoration(hintText: 'Ask NyumbaIQ Assistant...'),
                    onSubmitted: (value) {
                      if (value.trim().isEmpty) return;
                      aiProvider.sendMessage(value);
                      _controller.clear();
                    },
                  ),
                ),
                const SizedBox(width: 8),
                IconButton(
                  onPressed: aiProvider.isLoading
                      ? null
                      : () {
                          final text = _controller.text;
                          if (text.trim().isEmpty) return;
                          aiProvider.sendMessage(text);
                          _controller.clear();
                        },
                  icon: const Icon(Icons.send),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
