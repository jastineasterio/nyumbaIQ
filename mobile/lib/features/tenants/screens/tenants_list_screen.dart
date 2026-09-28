import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/tenants_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../core/widgets/empty_state_widget.dart';
import '../../../config/routes.dart';
import '../../../core/theme/app_text_styles.dart';

class TenantsListScreen extends StatefulWidget {
  const TenantsListScreen({super.key});

  @override
  State<TenantsListScreen> createState() => _TenantsListScreenState();
}

class _TenantsListScreenState extends State<TenantsListScreen> {
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<TenantsProvider>().fetchTenants();
    });
    _scrollController.addListener(_onScroll);
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      final provider = context.read<TenantsProvider>();
      if (provider.hasMore && !provider.isLoading) {
        provider.fetchTenants(page: provider.tenants.length ~/ 20 + 1);
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
      appBar: const CustomAppBar(title: 'Tenants'),
      drawer: const AppDrawer(),
      body: Consumer<TenantsProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.tenants.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.tenants.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: () => provider.fetchTenants(),
            );
          }
          if (provider.tenants.isEmpty) {
            return EmptyStateWidget(
              icon: Icons.people_rounded,
              title: 'No Tenants',
              message: 'Add tenants to your units.',
              actionLabel: 'Add Tenant',
              onAction: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Create tenant form coming soon')),
                );
              },
            );
          }
          return RefreshIndicator(
            onRefresh: () => provider.fetchTenants(),
            child: ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.all(16),
              itemCount: provider.tenants.length + (provider.hasMore ? 1 : 0),
              itemBuilder: (context, index) {
                if (index >= provider.tenants.length) {
                  return const Padding(
                    padding: EdgeInsets.all(16),
                    child: LoadingIndicator(),
                  );
                }
                final tenant = provider.tenants[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    contentPadding: const EdgeInsets.all(16),
                    leading: CircleAvatar(
                      backgroundColor: Theme.of(context).colorScheme.secondaryContainer,
                      child: Icon(
                        Icons.person_rounded,
                        color: Theme.of(context).colorScheme.secondary,
                      ),
                    ),
                    title: Text(
                      '${tenant['firstName'] ?? ''} ${tenant['lastName'] ?? ''}',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    subtitle: Text(tenant['email'] as String? ?? ''),
                    trailing: const Icon(Icons.chevron_right_rounded),
                    onTap: () {
                      final id = tenant['id']?.toString() ?? '';
                      Navigator.pushNamed(
                        context,
                        AppRoutes.tenantDetail,
                        arguments: id,
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
          if (auth.role == 'OWNER' || auth.role == 'MANAGER') {
            return FloatingActionButton(
              onPressed: () {
                Navigator.pushNamed(context, AppRoutes.tenantForm);
              },
              child: const Icon(Icons.add_rounded),
            );
          }
          return const SizedBox.shrink();
        },
      ),
      bottomNavigationBar: const AppBottomNav(currentIndex: 2),
    );
  }
}
