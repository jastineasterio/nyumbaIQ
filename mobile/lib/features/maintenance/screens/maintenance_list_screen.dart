import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../maintenance/providers/maintenance_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';

class MaintenanceListScreen extends StatelessWidget {
  const MaintenanceListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Maintenance'),
      drawer: const AppDrawer(),
      body: Consumer<MaintenanceProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.requests.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.requests.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: () => provider.fetchRequests(),
            );
          }
          return RefreshIndicator(
            onRefresh: () => provider.fetchRequests(),
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: provider.requests.length,
              itemBuilder: (context, index) {
                final request = provider.requests[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    title: Text(request.title),
                    subtitle: Text('${request.priority} | ${request.status}'),
                    trailing: const Icon(Icons.arrow_forward_ios_rounded, size: 16),
                    onTap: () {
                      Navigator.pushNamed(context, '/maintenance/detail', arguments: request.id);
                    },
                  ),
                );
              },
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          Navigator.pushNamed(context, '/maintenance/form');
        },
        child: const Icon(Icons.add_rounded),
      ),
      bottomNavigationBar: const AppBottomNav(currentIndex: -1),
    );
  }
}
