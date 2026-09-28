import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../smart_access/providers/smart_access_provider.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';

class LockDetailScreen extends StatelessWidget {
  final String lockId;

  const LockDetailScreen({super.key, required this.lockId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Lock Details'),
      body: Consumer<SmartAccessProvider>(
        builder: (context, provider, child) {
          return FutureBuilder(
            future: provider.getLockById(lockId),
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const LoadingIndicator(size: 40);
              }

              final lock = snapshot.data;
              if (lock == null) {
                return const Center(child: Text('Lock not found'));
              }

              return ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  _DetailTile(
                    icon: Icons.lock_rounded,
                    label: 'Name',
                    value: lock.name,
                  ),
                  _DetailTile(
                    icon: Icons.settings_rounded,
                    label: 'Model',
                    value: lock.model,
                  ),
                  _DetailTile(
                    icon: Icons.info_rounded,
                    label: 'Status',
                    value: lock.status,
                  ),
                  _DetailTile(
                    icon: Icons.lock_rounded,
                    label: 'Lock State',
                    value: lock.isLocked ? 'Locked' : 'Unlocked',
                    valueColor: lock.isLocked ? Colors.red : Colors.green,
                  ),
                  const SizedBox(height: 24),
                  Row(
                    children: [
                      Expanded(
                        child: ElevatedButton.icon(
                          onPressed: lock.isLocked ? null : () => provider.lock(lockId),
                          icon: const Icon(Icons.lock_rounded),
                          label: const Text('Lock'),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: ElevatedButton.icon(
                          onPressed: lock.isLocked ? () => provider.unlock(lockId) : null,
                          icon: const Icon(Icons.lock_open_rounded),
                          label: const Text('Unlock'),
                        ),
                      ),
                    ],
                  ),
                ],
              );
            },
          );
        },
      ),
    );
  }
}

class _DetailTile extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;
  final Color? valueColor;

  const _DetailTile({
    required this.icon,
    required this.label,
    required this.value,
    this.valueColor,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: ListTile(
        leading: Icon(icon, color: Theme.of(context).colorScheme.primary),
        title: Text(label, style: Theme.of(context).textTheme.bodySmall),
        trailing: Text(
          value,
          style: Theme.of(context).textTheme.titleMedium?.copyWith(
                color: valueColor,
                fontWeight: FontWeight.w600,
              ),
        ),
      ),
    );
  }
}
