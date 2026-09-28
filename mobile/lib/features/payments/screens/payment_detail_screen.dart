import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../payments/providers/payments_provider.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';

class PaymentDetailScreen extends StatelessWidget {
  final String paymentId;

  const PaymentDetailScreen({super.key, required this.paymentId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Payment Details'),
      body: Consumer<PaymentsProvider>(
        builder: (context, provider, child) {
          return FutureBuilder(
            future: provider.getPaymentById(paymentId),
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const LoadingIndicator(size: 40);
              }

              final payment = snapshot.data;
              if (payment == null) {
                return const Center(child: Text('Payment not found'));
              }

              return ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  _DetailTile(
                    icon: Icons.tag_rounded,
                    label: 'Reference',
                    value: payment.reference,
                  ),
                  _DetailTile(
                    icon: Icons.payments_rounded,
                    label: 'Amount',
                    value: '\$${payment.amount.toStringAsFixed(2)}',
                  ),
                  _DetailTile(
                    icon: Icons.payment_rounded,
                    label: 'Method',
                    value: payment.method,
                  ),
                  _DetailTile(
                    icon: Icons.info_rounded,
                    label: 'Status',
                    value: payment.status,
                    valueColor: payment.status == 'COMPLETED' ? Colors.green : Colors.orange,
                  ),
                  if (payment.transactionId != null)
                    _DetailTile(
                      icon: Icons.receipt_long_rounded,
                      label: 'Transaction ID',
                      value: payment.transactionId!,
                    ),
                  const SizedBox(height: 24),
                  Text(
                    'Allocations',
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 12),
                  ...payment.allocations.map((alloc) => Card(
                    margin: const EdgeInsets.only(bottom: 8),
                    child: ListTile(
                      title: Text('Invoice ${alloc.invoiceId.substring(0, 8)}'),
                      trailing: Text('\$${alloc.amount.toStringAsFixed(2)}'),
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
