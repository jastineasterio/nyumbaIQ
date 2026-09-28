import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../invoices/providers/invoices_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../config/routes.dart';

class InvoicesListScreen extends StatelessWidget {
  const InvoicesListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Invoices'),
      drawer: const AppDrawer(),
      body: Consumer<InvoicesProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.invoices.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.invoices.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: () => provider.fetchInvoices(),
            );
          }
          return RefreshIndicator(
            onRefresh: () => provider.fetchInvoices(),
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: provider.invoices.length,
              itemBuilder: (context, index) {
                final invoice = provider.invoices[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    title: Text(invoice.invoiceNumber),
                    subtitle: Text('\$${invoice.totalAmount.toStringAsFixed(2)} | Due: ${_formatDate(invoice.dueDate)}'),
                    trailing: Text(
                      invoice.status,
                      style: TextStyle(
                        color: invoice.status == 'PAID' ? Colors.green : Colors.orange,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    onTap: () {
                      Navigator.pushNamed(context, AppRoutes.invoiceDetail, arguments: invoice.id);
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

  String _formatDate(DateTime date) {
    return '${date.day}/${date.month}/${date.year}';
  }
}
