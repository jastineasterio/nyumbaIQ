import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:nyumbaiq_mobile/auth/providers/auth_provider.dart';
import 'package:nyumbaiq_mobile/config/routes.dart';
import 'package:nyumbaiq_mobile/core/theme/app_colors.dart';
import 'package:nyumbaiq_mobile/core/theme/app_text_styles.dart';

class AppDrawer extends StatelessWidget {
  const AppDrawer({super.key});

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context);
    final user = authProvider.user;
    final role = user?.role;

    return Drawer(
      child: SafeArea(
        child: Column(
          children: [
            UserAccountsDrawerHeader(
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                  colors: [AppColors.primary, AppColors.primaryDark],
                ),
              ),
              currentAccountPicture: CircleAvatar(
                backgroundColor: AppColors.accent,
                child: Text(
                  user != null
                      ? '${user.firstName[0]}${user.lastName[0]}'
                      : 'U',
                  style: AppTextStyles.titleLarge.copyWith(color: Colors.white),
                ),
              ),
              accountName: Text(
                user != null ? '${user.firstName} ${user.lastName}' : 'User',
                style: AppTextStyles.titleMedium.copyWith(color: Colors.white),
              ),
              accountEmail: Text(
                user?.email ?? '',
                style: AppTextStyles.bodySmall.copyWith(color: Colors.white70),
              ),
            ),
            Expanded(
              child: ListView(
                padding: EdgeInsets.zero,
                children: [
                  _buildNavItem(
                    context,
                    icon: Icons.dashboard_rounded,
                    label: 'Dashboard',
                    route: AppRoutes.dashboard,
                  ),
                  if (role == 'OWNER' || role == 'MANAGER') ...[
                    _buildNavItem(
                      context,
                      icon: Icons.apartment_rounded,
                      label: 'Properties',
                      route: AppRoutes.properties,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.location_city_rounded,
                      label: 'Buildings',
                      route: AppRoutes.buildings,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.layers_rounded,
                      label: 'Floors',
                      route: AppRoutes.floors,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.door_front_door_rounded,
                      label: 'Units',
                      route: AppRoutes.units,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.people_rounded,
                      label: 'Tenants',
                      route: AppRoutes.tenants,
                    ),
                  ],
                  if (role == 'OWNER')
                    _buildNavItem(
                      context,
                      icon: Icons.supervisor_account_rounded,
                      label: 'Managers',
                      route: AppRoutes.managers,
                    ),
                  _buildNavItem(
                    context,
                    icon: Icons.verified_user_rounded,
                    label: 'KYC',
                    route: AppRoutes.kyc,
                  ),
                  if (role == 'OWNER' || role == 'MANAGER')
                    _buildNavItem(
                      context,
                      icon: Icons.history_rounded,
                      label: 'Audit Logs',
                      route: AppRoutes.auditLogs,
                    ),
                  const Divider(),
                  if (role == 'OWNER' || role == 'MANAGER') ...[
                    _buildNavItem(
                      context,
                      icon: Icons.assignment_rounded,
                      label: 'Leases',
                      route: AppRoutes.leases,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.receipt_long_rounded,
                      label: 'Invoices',
                      route: AppRoutes.invoices,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.payments_rounded,
                      label: 'Payments',
                      route: AppRoutes.payments,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.money_rounded,
                      label: 'Billing',
                      route: AppRoutes.billing,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.home_repair_service_rounded,
                      label: 'Maintenance',
                      route: AppRoutes.maintenance,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.account_balance_wallet_rounded,
                      label: 'Expenses',
                      route: AppRoutes.expenses,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.smart_toy_rounded,
                      label: 'Smart Access',
                      route: AppRoutes.smartAccess,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.bar_chart_rounded,
                      label: 'Reports',
                      route: AppRoutes.reports,
                    ),
                  ],
                  if (role == 'TENANT') ...[
                    _buildNavItem(
                      context,
                      icon: Icons.assignment_rounded,
                      label: 'My Lease',
                      route: AppRoutes.leases,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.receipt_long_rounded,
                      label: 'My Invoices',
                      route: AppRoutes.invoices,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.payments_rounded,
                      label: 'Payments',
                      route: AppRoutes.payments,
                    ),
                    _buildNavItem(
                      context,
                      icon: Icons.home_repair_service_rounded,
                      label: 'Maintenance',
                      route: AppRoutes.maintenance,
                    ),
                  _buildNavItem(
                    context,
                    icon: Icons.swap_horiz_rounded,
                    label: 'Extensions',
                    route: AppRoutes.extensions,
                  ),
                    _buildNavItem(
                      context,
                      icon: Icons.notifications_rounded,
                      label: 'Notifications',
                      route: AppRoutes.notifications,
                    ),
                  ],
                  const Divider(),
                  _buildNavItem(
                    context,
                    icon: Icons.person_rounded,
                    label: 'Profile',
                    route: AppRoutes.profile,
                  ),
                  _buildNavItem(
                    context,
                    icon: Icons.lock_rounded,
                    label: 'Change Password',
                    route: AppRoutes.changePassword,
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.all(16),
              child: SizedBox(
                width: double.infinity,
                child: OutlinedButton.icon(
                  onPressed: () async {
                    Navigator.pop(context);
                    await authProvider.logout();
                    if (context.mounted) {
                      Navigator.pushReplacementNamed(context, AppRoutes.login);
                    }
                  },
                  icon: const Icon(Icons.logout_rounded),
                  label: const Text('Logout'),
                  style: OutlinedButton.styleFrom(
                    foregroundColor: AppColors.error,
                    side: const BorderSide(color: AppColors.error),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildNavItem(
    BuildContext context, {
    required IconData icon,
    required String label,
    required String route,
  }) {
    final isActive = ModalRoute.of(context)?.settings.name == route;

    return ListTile(
      leading: Icon(
        icon,
        color: isActive ? AppColors.primary : null,
      ),
      title: Text(
        label,
        style: TextStyle(
          color: isActive ? AppColors.primary : null,
          fontWeight: isActive ? FontWeight.w600 : null,
        ),
      ),
      selected: isActive,
      selectedTileColor: AppColors.primary.withOpacity(0.08),
      onTap: () {
        Navigator.pop(context);
        Navigator.pushReplacementNamed(context, route);
      },
    );
  }
}

