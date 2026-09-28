import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../smart_access/providers/smart_access_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';

class SmartAccessScreen extends StatelessWidget {
  const SmartAccessScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Smart Access'),
      drawer: const AppDrawer(),
      body: Consumer<SmartAccessProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.locks.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.locks.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: provider.fetchLocks,
            );
          }
          return RefreshIndicator(
            onRefresh: provider.fetchLocks,
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: provider.locks.length,
              itemBuilder: (context, index) {
                final lock = provider.locks[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    title: Text(lock.name),
                    subtitle: Text(lock.model),
                    trailing: Icon(
                      lock.isLocked ? Icons.lock_rounded : Icons.lock_open_rounded,
                      color: lock.isLocked ? Colors.red : Colors.green,
                    ),
                    onTap: () {
                      Navigator.pushNamed(context, '/smart-access/detail', arguments: lock.id);
                    },
                  ),
                );
              },
            ),
          );
        },
      ),
      bottomNavigationBar: const AppBottomNav(currentIndex: -1),
    );
  }
}
