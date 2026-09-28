import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../reports/providers/reports_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/theme/app_text_styles.dart';
import '../../../config/routes.dart';

class ReportsScreen extends StatelessWidget {
  const ReportsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Reports'),
      drawer: const AppDrawer(),
      body: Consumer<ReportsProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null) {
            return Center(
              child: Column(
                children: [
                  Text(provider.error!, textAlign: TextAlign.center),
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: provider.refreshAll,
                    child: const Text('Retry'),
                  ),
                ],
              ),
            );
          }
          return RefreshIndicator(
            onRefresh: provider.refreshAll,
            child: ListView(
              padding: const EdgeInsets.all(16),
              children: [
                _ReportCard(
                  title: 'Income Summary',
                  value: provider.incomeReport != null
                      ? '\$${provider.incomeReport!.totalIncome.toStringAsFixed(2)}'
                      : '--',
                  icon: Icons.trending_up_rounded,
                  color: Colors.green,
                  onTap: () {
                    if (provider.incomeReport != null) {
                      Navigator.pushNamed(context, AppRoutes.financialSummary);
                    }
                  },
                ),
                _ReportCard(
                  title: 'Expense Summary',
                  value: provider.expenseReport != null
                      ? '\$${provider.expenseReport!.totalExpense.toStringAsFixed(2)}'
                      : '--',
                  icon: Icons.trending_down_rounded,
                  color: Colors.red,
                  onTap: () {
                    if (provider.expenseReport != null) {
                      Navigator.pushNamed(context, AppRoutes.financialSummary);
                    }
                  },
                ),
                _ReportCard(
                  title: 'Outstanding Rent',
                  value: provider.outstandingReport != null
                      ? '\$${provider.outstandingReport!.totalIncome.toStringAsFixed(2)}'
                      : '--',
                  icon: Icons.pending_actions_rounded,
                  color: Colors.orange,
                  onTap: () {
                    if (provider.outstandingReport != null) {
                      Navigator.pushNamed(context, AppRoutes.financialSummary);
                    }
                  },
                ),
              ],
            ),
          );
        },
      ),
      bottomNavigationBar: const AppBottomNav(currentIndex: -1),
    );
  }
}

class _ReportCard extends StatelessWidget {
  final String title;
  final String value;
  final IconData icon;
  final Color color;
  final VoidCallback? onTap;

  const _ReportCard({
    required this.title,
    required this.value,
    required this.icon,
    required this.color,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      child: InkWell(
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Row(
            children: [
              Icon(icon, color: color, size: 36),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    const SizedBox(height: 4),
                    Text(
                      value,
                      style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                            color: color,
                            fontWeight: FontWeight.bold,
                          ),
                    ),
                  ],
                ),
              ),
              if (onTap != null)
                const Icon(Icons.arrow_forward_ios_rounded, size: 16),
            ],
          ),
        ),
      ),
    );
  }
}
