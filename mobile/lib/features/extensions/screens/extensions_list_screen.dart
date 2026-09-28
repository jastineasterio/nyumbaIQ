import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../extensions/providers/extensions_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../config/routes.dart';

class ExtensionsListScreen extends StatelessWidget {
  const ExtensionsListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Rental Extensions'),
      drawer: const AppDrawer(),
      body: Consumer<ExtensionsProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.extensions.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.extensions.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: () => provider.fetchExtensions(),
            );
          }
          return RefreshIndicator(
            onRefresh: () => provider.fetchExtensions(),
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: provider.extensions.length,
              itemBuilder: (context, index) {
                final extension = provider.extensions[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    title: Text('Extension ${extension.id.substring(0, 8)}'),
                    subtitle: Text('Ends: ${_formatDate(extension.proposedEndDate)}'),
                    trailing: Text(
                      extension.status,
                      style: TextStyle(
                        color: extension.status == 'APPROVED' ? Colors.green : Colors.orange,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                );
              },
            ),
          );
        },
      ),
      floatingActionButton: Consumer<AuthProvider>(
        builder: (context, auth, _) {
          if (auth.role == 'TENANT') {
            return FloatingActionButton(
              onPressed: () {
                Navigator.pushNamed(context, AppRoutes.extensionForm);
              },
              child: const Icon(Icons.add_rounded),
            );
          }
          return const SizedBox.shrink();
        },
      ),
      bottomNavigationBar: const AppBottomNav(currentIndex: -1),
    );
  }

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year}';
  }
}
