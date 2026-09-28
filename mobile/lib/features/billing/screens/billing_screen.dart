import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../billing/providers/billing_provider.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';

class BillingScreen extends StatelessWidget {
  const BillingScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Billing'),
      body: Consumer<BillingProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.schedules.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.schedules.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: provider.fetchSchedules,
            );
          }
          return RefreshIndicator(
            onRefresh: provider.fetchSchedules,
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: provider.schedules.length,
              itemBuilder: (context, index) {
                final schedule = provider.schedules[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    title: Text('Schedule ${schedule.id.substring(0, 8)}'),
                    subtitle: Text('\$${schedule.amount.toStringAsFixed(2)} - ${schedule.frequency}'),
                    trailing: Text(
                      schedule.status,
                      style: TextStyle(
                        color: schedule.status == 'PAID' ? Colors.green : Colors.orange,
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
    );
  }
}
