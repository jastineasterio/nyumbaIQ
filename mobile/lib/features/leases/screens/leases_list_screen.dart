import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../leases/providers/leases_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../config/routes.dart';

class LeasesListScreen extends StatelessWidget {
  const LeasesListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Leases'),
      drawer: const AppDrawer(),
      body: Consumer<LeasesProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.leases.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.leases.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: () => provider.fetchLeases(),
            );
          }
          return RefreshIndicator(
            onRefresh: () => provider.fetchLeases(),
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: provider.leases.length,
              itemBuilder: (context, index) {
                final lease = provider.leases[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    title: Text('Lease ${lease.id.substring(0, 8)}'),
                    subtitle: Text('Rent: \$${lease.rentAmount.toStringAsFixed(2)} | ${lease.status}'),
                    trailing: const Icon(Icons.arrow_forward_ios_rounded, size: 16),
                    onTap: () {
                      Navigator.pushNamed(context, AppRoutes.leaseDetail, arguments: lease.id);
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
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Create lease coming soon')),
                );
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
}
