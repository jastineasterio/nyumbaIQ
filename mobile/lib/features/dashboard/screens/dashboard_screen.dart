import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/dashboard_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../config/routes.dart';
import '../../../core/theme/app_text_styles.dart';
import '../../leases/providers/leases_provider.dart';
import '../../invoices/providers/invoices_provider.dart';

class DashboardScreen extends StatelessWidget {
  const DashboardScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Dashboard'),
      drawer: const AppDrawer(),
      body: Consumer2<DashboardProvider, AuthProvider>(
        builder: (context, dashboard, auth, child) {
          if (dashboard.isLoading && dashboard.stats == null) {
            return const LoadingIndicator(size: 40);
          }
          if (dashboard.error != null && dashboard.stats == null) {
            return CustomErrorWidget(
              message: dashboard.error!,
              onRetry: dashboard.fetchStats,
            );
          }
          final stats = dashboard.stats ?? {};
          final role = auth.role;

          return RefreshIndicator(
            onRefresh: dashboard.fetchStats,
            child: ListView(
              padding: const EdgeInsets.all(16),
              children: [
                Text(
                  'Welcome, ${auth.user?.firstName ?? 'User'}',
                  style: Theme.of(context).textTheme.headlineSmall,
                ),
                const SizedBox(height: 16),
                if (role == 'TENANT') ...[
                  _TenantDashboard(stats: stats),
                ] else if (role == 'OWNER' || role == 'MANAGER') ...[
                  _OwnerManagerDashboard(stats: stats),
                ] else ...[
                  _DefaultDashboard(stats: stats),
                ],
              ],
            ),
          );
        },
      ),
      bottomNavigationBar: const AppBottomNav(currentIndex: 0),
    );
  }
}

class _TenantDashboard extends StatelessWidget {
  final Map<String, dynamic> stats;

  const _TenantDashboard({required this.stats});

  @override
  Widget build(BuildContext context) {
    final balance = (stats['rentBalance'] as num?)?.toDouble() ?? 0.0;
    final dueDate = stats['nextDueDate'] as String? ?? '--';
    final status = stats['leaseStatus'] as String? ?? '--';

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Card(
        color: Theme.of(context).colorScheme.primaryContainer,
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Rent Balance',
                style: Theme.of(context).textTheme.titleMedium,
              ),
              const SizedBox(height: 8),
              Text(
                '\$${balance.toStringAsFixed(2)}',
                style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                      color: balance > 0 ? Colors.red : Colors.green,
                      fontWeight: FontWeight.bold,
                    ),
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  Icon(Icons.calendar_today_rounded, size: 16, color: Theme.of(context).colorScheme.onSurfaceVariant),
                  const SizedBox(width: 8),
                  Text(
                    'Due: $dueDate',
                    style: AppTextStyles.caption,
                  ),
                ],
              ),
              const SizedBox(height: 4),
              Row(
                children: [
                  Icon(Icons.info_rounded, size: 16, color: Theme.of(context).colorScheme.onSurfaceVariant),
                  const SizedBox(width: 8),
                  Text(
                    'Status: $status',
                    style: AppTextStyles.caption,
                  ),
                ],
              ),
            ],
          ),
        ),
        ),
        const SizedBox(height: 16),
        Text(
          'Quick Actions',
          style: Theme.of(context).textTheme.titleMedium,
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: _QuickActionCard(
                icon: Icons.payments_rounded,
                label: 'Pay Rent',
                color: Colors.green,
                onTap: () => _navigateTo(context, '/payments'),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _QuickActionCard(
                icon: Icons.home_repair_service_rounded,
                label: 'Request Repair',
                color: Colors.orange,
                onTap: () => _navigateTo(context, '/maintenance/form'),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _QuickActionCard(
                icon: Icons.swap_horiz_rounded,
                label: 'Extend Lease',
                color: Colors.blue,
                onTap: () => _navigateTo(context, '/extensions/form'),
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),
        Text(
          'Leases',
          style: Theme.of(context).textTheme.titleMedium,
        ),
        const SizedBox(height: 12),
        Consumer<LeasesProvider>(
          builder: (context, provider, _) {
            if (provider.isLoading && provider.leases.isEmpty) {
              return const Center(child: Padding(
                padding: EdgeInsets.all(16.0),
                child: LoadingIndicator(size: 30),
              ));
            }
            return Column(
              children: provider.leases.take(3).map((lease) => Card(
                margin: const EdgeInsets.only(bottom: 8),
                child: ListTile(
                  title: Text('Lease ${lease.id.substring(0, 8)}'),
                  subtitle: Text('Rent: \$${lease.rentAmount.toStringAsFixed(2)}'),
                  trailing: Text(
                    lease.status,
                    style: TextStyle(
                      color: lease.status == 'ACTIVE' ? Colors.green : Colors.orange,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  onTap: () => _navigateTo(context, '/leases/${lease.id}'),
                ),
              )).toList(),
            );
          },
        ),
      ],
    );
  }

  void _navigateTo(BuildContext context, String route) {
    Navigator.pushNamed(context, route);
  }
}

class _OwnerManagerDashboard extends StatelessWidget {
  final Map<String, dynamic> stats;

  const _OwnerManagerDashboard({required this.stats});

  @override
  Widget build(BuildContext context) {
    final income = (stats['totalIncome'] as num?)?.toDouble() ?? 0.0;
    final expenses = (stats['totalExpenses'] as num?)?.toDouble() ?? 0.0;
    final net = income - expenses;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Financial Overview',
          style: Theme.of(context).textTheme.titleMedium,
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: _StatCard(
                title: 'Income',
                value: '\$${income.toStringAsFixed(2)}',
                color: Colors.green,
                icon: Icons.trending_up_rounded,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _StatCard(
                title: 'Expenses',
                value: '\$${expenses.toStringAsFixed(2)}',
                color: Colors.red,
                icon: Icons.trending_down_rounded,
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        _StatCard(
          title: 'Net',
          value: '\$${net.toStringAsFixed(2)}',
          color: net >= 0 ? Colors.blue : Colors.red,
          icon: Icons.account_balance_rounded,
        ),
        const SizedBox(height: 16),
        Text(
          'Quick Actions',
          style: Theme.of(context).textTheme.titleMedium,
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: _QuickActionCard(
                icon: Icons.assignment_rounded,
                label: 'Leases',
                color: Theme.of(context).colorScheme.primary,
                onTap: () => _navigateTo(context, '/leases'),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _QuickActionCard(
                icon: Icons.receipt_long_rounded,
                label: 'Invoices',
                color: Theme.of(context).colorScheme.secondary,
                onTap: () => _navigateTo(context, '/invoices'),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _QuickActionCard(
                icon: Icons.home_repair_service_rounded,
                label: 'Maintenance',
                color: Colors.orange,
                onTap: () => _navigateTo(context, '/maintenance'),
              ),
            ),
          ],
        ),
      ],
    );
  }

  void _navigateTo(BuildContext context, String route) {
    Navigator.pushNamed(context, route);
  }
}

class _DefaultDashboard extends StatelessWidget {
  final Map<String, dynamic> stats;

  const _DefaultDashboard({required this.stats});

  @override
  Widget build(BuildContext context) {
    return GridView.count(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      crossAxisCount: 2,
      mainAxisSpacing: 12,
      crossAxisSpacing: 12,
      childAspectRatio: 1.6,
      children: [
        _StatCard(
          title: 'Properties',
          value: '${stats['totalProperties'] ?? 0}',
          icon: Icons.apartment_rounded,
          color: Theme.of(context).colorScheme.primary,
        ),
        _StatCard(
          title: 'Buildings',
          value: '${stats['totalBuildings'] ?? 0}',
          icon: Icons.location_city_rounded,
          color: Theme.of(context).colorScheme.secondary,
        ),
        _StatCard(
          title: 'Floors',
          value: '${stats['totalFloors'] ?? 0}',
          icon: Icons.layers_rounded,
          color: Theme.of(context).colorScheme.tertiary,
        ),
        _StatCard(
          title: 'Units',
          value: '${stats['totalUnits'] ?? 0}',
          icon: Icons.door_front_door_rounded,
          color: Theme.of(context).colorScheme.primary,
        ),
        _StatCard(
          title: 'Occupied',
          value: '${stats['occupiedUnits'] ?? 0}',
          icon: Icons.home_rounded,
          color: Theme.of(context).colorScheme.secondary,
        ),
        _StatCard(
          title: 'Vacant',
          value: '${stats['vacantUnits'] ?? 0}',
          icon: Icons.home_work_rounded,
          color: Theme.of(context).colorScheme.tertiary,
        ),
      ],
    );
  }
}

class _StatCard extends StatelessWidget {
  final String title;
  final String value;
  final IconData icon;
  final Color color;

  const _StatCard({
    required this.title,
    required this.value,
    required this.icon,
    required this.color,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            Icon(icon, color: color, size: 32),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    value,
                    style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                          color: color,
                          fontWeight: FontWeight.bold,
                        ),
                  ),
                  Text(
                    title,
                    style: AppTextStyles.caption,
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _QuickActionCard extends StatelessWidget {
  final IconData icon;
  final String label;
  final Color color;
  final VoidCallback onTap;

  const _QuickActionCard({
    required this.icon,
    required this.label,
    required this.color,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            children: [
              Icon(icon, color: color, size: 28),
              const SizedBox(height: 8),
              Text(
                label,
                textAlign: TextAlign.center,
                style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      fontWeight: FontWeight.w600,
                    ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
