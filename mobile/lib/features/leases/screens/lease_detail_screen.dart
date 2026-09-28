import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../leases/providers/leases_provider.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../config/routes.dart';

class LeaseDetailScreen extends StatelessWidget {
  final String leaseId;

  const LeaseDetailScreen({super.key, required this.leaseId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Lease Details'),
      body: Consumer<LeasesProvider>(
        builder: (context, provider, child) {
          final lease = provider.leases.where((l) => l.id == leaseId).firstOrNull;

          if (provider.isLoading && lease == null) {
            return const LoadingIndicator(size: 40);
          }

          if (lease == null) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.error_outline_rounded, size: 64, color: Colors.grey),
                  const SizedBox(height: 16),
                  Text(
                    'Lease not found',
                    style: Theme.of(context).textTheme.titleLarge,
                  ),
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: () => provider.fetchLeases(),
                    child: const Text('Refresh'),
                  ),
                ],
              ),
            );
          }

          return ListView(
            padding: const EdgeInsets.all(16),
            children: [
              _DetailTile(
                icon: Icons.calendar_today_rounded,
                label: 'Start Date',
                value: '${lease.startDate.day}/${lease.startDate.month}/${lease.startDate.year}',
              ),
              _DetailTile(
                icon: Icons.event_rounded,
                label: 'End Date',
                value: '${lease.endDate.day}/${lease.endDate.month}/${lease.endDate.year}',
              ),
              _DetailTile(
                icon: Icons.attach_money_rounded,
                label: 'Rent Amount',
                value: '\$${lease.rentAmount.toStringAsFixed(2)}',
              ),
              _DetailTile(
                icon: Icons.savings_rounded,
                label: 'Deposit',
                value: '\$${lease.depositAmount.toStringAsFixed(2)}',
              ),
              _DetailTile(
                icon: Icons.info_rounded,
                label: 'Status',
                value: lease.status,
                valueColor: lease.status == 'ACTIVE' ? Colors.green : Colors.orange,
              ),
              if (lease.notes != null) ...[
                const SizedBox(height: 16),
                Text(
                  'Notes',
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                const SizedBox(height: 8),
                Text(lease.notes!),
              ],
            ],
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
