import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/managers_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../core/widgets/empty_state_widget.dart';
import '../../../config/routes.dart';
import '../../../core/theme/app_text_styles.dart';

class ManagersListScreen extends StatefulWidget {
  const ManagersListScreen({super.key});

  @override
  State<ManagersListScreen> createState() => _ManagersListScreenState();
}

class _ManagersListScreenState extends State<ManagersListScreen> {
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<ManagersProvider>().fetchManagers();
    });
    _scrollController.addListener(_onScroll);
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      final provider = context.read<ManagersProvider>();
      if (provider.hasMore && !provider.isLoading) {
        provider.fetchManagers(page: provider.managers.length ~/ 20 + 1);
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
      appBar: const CustomAppBar(title: 'Managers'),
      drawer: const AppDrawer(),
      body: Consumer<ManagersProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.managers.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.managers.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: () => provider.fetchManagers(),
            );
          }
          if (provider.managers.isEmpty) {
            return EmptyStateWidget(
              icon: Icons.supervisor_account_rounded,
              title: 'No Managers',
              message: 'Add property managers.',
              actionLabel: 'Add Manager',
              onAction: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Create manager form coming soon')),
                );
              },
            );
          }
          return RefreshIndicator(
            onRefresh: () => provider.fetchManagers(),
            child: ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.all(16),
              itemCount: provider.managers.length + (provider.hasMore ? 1 : 0),
              itemBuilder: (context, index) {
                if (index >= provider.managers.length) {
                  return const Padding(
                    padding: EdgeInsets.all(16),
                    child: LoadingIndicator(),
                  );
                }
                final manager = provider.managers[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    contentPadding: const EdgeInsets.all(16),
                    leading: CircleAvatar(
                      backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                      child: Icon(
                        Icons.person_rounded,
                        color: Theme.of(context).colorScheme.primary,
                      ),
                    ),
                    title: Text(
                      '${manager['firstName'] ?? ''} ${manager['lastName'] ?? ''}',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    subtitle: Text(manager['email'] as String? ?? ''),
                    trailing: const Icon(Icons.chevron_right_rounded),
                    onTap: () {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('Manager detail view coming soon')),
                      );
                    },
                  ),
                );
              },
            ),
          );
        },
      ),
      floatingActionButton: Consumer<AuthProvider>(
        builder: (context, auth, _) {
          if (auth.role == 'OWNER') {
            return FloatingActionButton(
              onPressed: () {
                Navigator.pushNamed(context, AppRoutes.managerForm);
              },
              child: const Icon(Icons.add_rounded),
            );
          }
          return const SizedBox.shrink();
        },
      ),
    );
  }
}
