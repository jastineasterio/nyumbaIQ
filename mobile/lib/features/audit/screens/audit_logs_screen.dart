import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/audit_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../core/widgets/empty_state_widget.dart';
import '../../../core/theme/app_text_styles.dart';

class AuditLogsScreen extends StatefulWidget {
  const AuditLogsScreen({super.key});

  @override
  State<AuditLogsScreen> createState() => _AuditLogsScreenState();
}

class _AuditLogsScreenState extends State<AuditLogsScreen> {
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<AuditProvider>().fetchAuditLogs();
    });
    _scrollController.addListener(_onScroll);
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      final provider = context.read<AuditProvider>();
      if (provider.hasMore && !provider.isLoading) {
        provider.fetchAuditLogs(page: provider.logs.length ~/ 20 + 1);
      }
    }
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Audit Logs'),
      drawer: const AppDrawer(),
      body: Consumer<AuditProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.logs.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.logs.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: () => provider.fetchAuditLogs(),
            );
          }
          if (provider.logs.isEmpty) {
            return EmptyStateWidget(
              icon: Icons.history_rounded,
              title: 'No Logs',
              message: 'Audit logs will appear here.',
            );
          }
          return RefreshIndicator(
            onRefresh: () => provider.fetchAuditLogs(),
            child: ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.all(16),
              itemCount: provider.logs.length + (provider.hasMore ? 1 : 0),
              itemBuilder: (context, index) {
                if (index >= provider.logs.length) {
                  return const Padding(
                    padding: EdgeInsets.all(16),
                    child: LoadingIndicator(),
                  );
                }
                final log = provider.logs[index];
                final action = log['action'] as String? ?? 'UNKNOWN';
                final timestamp = log['timestamp'] as String? ?? '';
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    contentPadding: const EdgeInsets.all(16),
                    leading: CircleAvatar(
                      backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                      child: Icon(
                        Icons.history_rounded,
                        color: Theme.of(context).colorScheme.primary,
                      ),
                    ),
                    title: Text(
                      action.replaceAll('_', ' ').toUpperCase(),
                      style: Theme.of(context).textTheme.titleSmall,
                    ),
                    subtitle: Text(
                      log['userName'] as String? ?? 'Unknown',
                    ),
                    trailing: Text(
                      timestamp.split('T').first,
                      style: AppTextStyles.caption,
                    ),
                  ),
                );
              },
            ),
          );
        },
      ),
    );
  }
}
