import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../payments/providers/payments_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../config/routes.dart';

class PaymentsListScreen extends StatelessWidget {
  const PaymentsListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Payments'),
      drawer: const AppDrawer(),
      body: Consumer<PaymentsProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.payments.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.payments.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: () => provider.fetchPayments(),
            );
          }
          return RefreshIndicator(
            onRefresh: () => provider.fetchPayments(),
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: provider.payments.length,
              itemBuilder: (context, index) {
                final payment = provider.payments[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    title: Text(payment.reference),
                    subtitle: Text('\$${payment.amount.toStringAsFixed(2)} | ${payment.method}'),
                    trailing: Text(
                      payment.status,
                      style: TextStyle(
                        color: payment.status == 'COMPLETED' ? Colors.green : Colors.orange,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    onTap: () {
                      Navigator.pushNamed(context, AppRoutes.paymentDetail, arguments: payment.id);
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
