import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../reports/providers/reports_provider.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/theme/app_text_styles.dart';

class FinancialSummaryScreen extends StatelessWidget {
  const FinancialSummaryScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Financial Summary'),
          bottom: const TabBar(
            tabs: [
              Tab(text: 'Income'),
              Tab(text: 'Expenses'),
              Tab(text: 'Outstanding'),
            ],
          ),
        ),
        body: Consumer<ReportsProvider>(
          builder: (context, provider, child) {
            return TabBarView(
              children: [
                _buildReportView(context, provider.incomeReport, 'Income'),
                _buildReportView(context, provider.expenseReport, 'Expenses'),
                _buildReportView(context, provider.outstandingReport, 'Outstanding'),
              ],
            );
          },
        ),
      ),
    );
  }

  Widget _buildReportView(BuildContext context, dynamic report, String title) {
    if (report == null) {
      return Center(
        child: Column(
          children: [
            const Icon(Icons.bar_chart_rounded, size: 64, color: Colors.grey),
            const SizedBox(height: 16),
            Text(
              'No $title data available',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: context.read<ReportsProvider>().refreshAll,
              child: const Text('Refresh'),
            ),
          ],
        ),
      );
    }

    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Card(
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                '$title Total',
                style: Theme.of(context).textTheme.titleMedium,
              ),
              const SizedBox(height: 8),
              Text(
                '\$${report.totalIncome.toStringAsFixed(2)}',
                style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                      color: Theme.of(context).colorScheme.primary,
                      fontWeight: FontWeight.bold,
                    ),
              ),
            ],
          ),
        ),
        ),
        const SizedBox(height: 24),
        Text(
          'Entries',
          style: Theme.of(context).textTheme.titleMedium,
        ),
        const SizedBox(height: 12),
        ...report.entries.map((entry) => Card(
          margin: const EdgeInsets.only(bottom: 8),
          child: ListTile(
            title: Text(entry.label),
            trailing: Text('\$${entry.amount.toStringAsFixed(2)}'),
          ),
        )),
      ],
    );
  }
}
