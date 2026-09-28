import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../expenses/providers/expenses_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../config/routes.dart';

class ExpensesListScreen extends StatelessWidget {
  const ExpensesListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Expenses'),
      drawer: const AppDrawer(),
      body: Consumer<ExpensesProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.expenses.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.expenses.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: () => provider.fetchExpenses(),
            );
          }
          return RefreshIndicator(
            onRefresh: () => provider.fetchExpenses(),
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: provider.expenses.length,
              itemBuilder: (context, index) {
                final expense = provider.expenses[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    title: Text(expense.description),
                    subtitle: Text('\$${expense.amount.toStringAsFixed(2)} | ${expense.category}'),
                    trailing: Text(
                      expense.status,
                      style: TextStyle(
                        color: expense.status == 'PAID' ? Colors.green : Colors.orange,
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
      floatingActionButton: Consumer<AuthProvider>(
        builder: (context, auth, _) {
          if (auth.role == 'OWNER' || auth.role == 'MANAGER') {
            return FloatingActionButton(
              onPressed: () {
                Navigator.pushNamed(context, AppRoutes.expenseForm);
              },
              child: const Icon(Icons.add_rounded),
            );
          }
          return const SizedBox.shrink();
        },
      ),
      bottomNavigationBar: const AppBottomNav(currentIndex: -1),
    );
  }
}
