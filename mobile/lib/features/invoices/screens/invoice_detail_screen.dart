import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../invoices/providers/invoices_provider.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';

class InvoiceDetailScreen extends StatelessWidget {
  final String invoiceId;

  const InvoiceDetailScreen({super.key, required this.invoiceId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Invoice Details'),
      body: Consumer<InvoicesProvider>(
        builder: (context, provider, child) {
          return FutureBuilder(
            future: provider.getInvoiceById(invoiceId),
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const LoadingIndicator(size: 40);
              }

              final invoice = snapshot.data;
              if (invoice == null) {
                return const Center(child: Text('Invoice not found'));
              }

              return ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  _DetailTile(
                    icon: Icons.confirmation_number_rounded,
                    label: 'Invoice Number',
                    value: invoice.invoiceNumber,
                  ),
                  _DetailTile(
                    icon: Icons.calendar_today_rounded,
                    label: 'Due Date',
                    value: '${invoice.dueDate.day}/${invoice.dueDate.month}/${invoice.dueDate.year}',
                  ),
                  _DetailTile(
                    icon: Icons.attach_money_rounded,
                    label: 'Total Amount',
                    value: '\$${invoice.totalAmount.toStringAsFixed(2)}',
                  ),
                  _DetailTile(
                    icon: Icons.payments_rounded,
                    label: 'Paid Amount',
                    value: '\$${invoice.paidAmount.toStringAsFixed(2)}',
                  ),
                  _DetailTile(
                    icon: Icons.account_balance_wallet_rounded,
                    label: 'Balance',
                    value: '\$${invoice.balance.toStringAsFixed(2)}',
                    valueColor: invoice.balance > 0 ? Colors.red : Colors.green,
                  ),
                  _DetailTile(
                    icon: Icons.info_rounded,
                    label: 'Status',
                    value: invoice.status,
                    valueColor: invoice.status == 'PAID' ? Colors.green : Colors.orange,
                  ),
                  const SizedBox(height: 24),
                  Text(
                    'Items',
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                  const SizedBox(height: 12),
                  ...invoice.items.map((item) => Card(
                    margin: const EdgeInsets.only(bottom: 8),
                    child: ListTile(
                      title: Text(item.description),
                      trailing: Text('\$${item.amount.toStringAsFixed(2)}'),
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
