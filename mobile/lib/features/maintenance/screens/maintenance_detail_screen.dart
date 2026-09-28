import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../maintenance/providers/maintenance_provider.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';

class MaintenanceDetailScreen extends StatelessWidget {
  final String requestId;

  const MaintenanceDetailScreen({super.key, required this.requestId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Request Details'),
      body: Consumer<MaintenanceProvider>(
        builder: (context, provider, child) {
          return FutureBuilder(
            future: provider.getRequestById(requestId),
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const LoadingIndicator(size: 40);
              }

              final request = snapshot.data;
              if (request == null) {
                return const Center(child: Text('Request not found'));
              }

              return ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  _DetailTile(
                    icon: Icons.title_rounded,
                    label: 'Title',
                    value: request.title,
                  ),
                  _DetailTile(
                    icon: Icons.description_rounded,
                    label: 'Description',
                    value: request.description,
                  ),
                  _DetailTile(
                    icon: Icons.warning_rounded,
                    label: 'Priority',
                    value: request.priority,
                    valueColor: request.priority == 'URGENT' ? Colors.red : null,
                  ),
                  _DetailTile(
                    icon: Icons.info_rounded,
                    label: 'Status',
                    value: request.status,
                    valueColor: request.status == 'COMPLETED' ? Colors.green : Colors.orange,
                  ),
                  if (request.assignedTo != null)
                    _DetailTile(
                      icon: Icons.person_rounded,
                      label: 'Assigned To',
                      value: request.assignedTo!,
                    ),
                  const SizedBox(height: 24),
                  Text(
                    'Costs',
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 12),
                  ...request.costs.map((cost) => Card(
                    margin: const EdgeInsets.only(bottom: 8),
                    child: ListTile(
                      title: Text(cost.description),
                      subtitle: Text(cost.category),
                      trailing: Text('\$${cost.amount.toStringAsFixed(2)}'),
                    ),
                  )),
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
