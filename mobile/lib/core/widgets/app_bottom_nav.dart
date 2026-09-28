import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../auth/providers/auth_provider.dart';
import '../../config/routes.dart';
import '../theme/app_colors.dart';

class AppBottomNav extends StatelessWidget {
  final int currentIndex;

  const AppBottomNav({super.key, required this.currentIndex});

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context);
    final role = authProvider.user?.role;

    final items = <BottomNavigationBarItem>[];

    if (role == 'OWNER' || role == 'MANAGER' || role == 'TENANT') {
      items.add(
        const BottomNavigationBarItem(
          icon: Icon(Icons.dashboard_rounded),
          activeIcon: Icon(Icons.dashboard_rounded),
          label: 'Dashboard',
        ),
      );
    }

    if (role == 'OWNER' || role == 'MANAGER') {
      items.add(
        const BottomNavigationBarItem(
          icon: Icon(Icons.apartment_rounded),
          activeIcon: Icon(Icons.apartment_rounded),
          label: 'Properties',
        ),
      );
      items.add(
        const BottomNavigationBarItem(
          icon: Icon(Icons.people_rounded),
          activeIcon: Icon(Icons.people_rounded),
          label: 'Tenants',
        ),
      );
      items.add(
        const BottomNavigationBarItem(
          icon: Icon(Icons.receipt_long_rounded),
          activeIcon: Icon(Icons.receipt_long_rounded),
          label: 'Invoices',
        ),
      );
      items.add(
        const BottomNavigationBarItem(
          icon: Icon(Icons.bar_chart_rounded),
          activeIcon: Icon(Icons.bar_chart_rounded),
          label: 'Reports',
        ),
      );
    }

    if (role == 'TENANT') {
      items.add(
        const BottomNavigationBarItem(
          icon: Icon(Icons.receipt_long_rounded),
          activeIcon: Icon(Icons.receipt_long_rounded),
          label: 'Invoices',
        ),
      );
      items.add(
        const BottomNavigationBarItem(
          icon: Icon(Icons.notifications_rounded),
          activeIcon: Icon(Icons.notifications_rounded),
          label: 'Alerts',
        ),
      );
    }

    items.add(
      const BottomNavigationBarItem(
        icon: Icon(Icons.person_rounded),
        activeIcon: Icon(Icons.person_rounded),
        label: 'Profile',
      ),
    );

    return BottomNavigationBar(
      currentIndex: currentIndex,
      onTap: (index) {
        String route;
        switch (index) {
          case 0:
            route = AppRoutes.dashboard;
            break;
          case 1:
            route = role == 'TENANT' ? AppRoutes.invoices : AppRoutes.properties;
            break;
          case 2:
            route = role == 'TENANT' ? AppRoutes.notifications : AppRoutes.tenants;
            break;
          case 3:
            route = role == 'TENANT' ? AppRoutes.profile : AppRoutes.invoices;
            break;
          case 4:
            route = role == 'TENANT' ? AppRoutes.profile : AppRoutes.reports;
            break;
          default:
            route = AppRoutes.profile;
        }
        if (ModalRoute.of(context)?.settings.name != route) {
          Navigator.pushReplacementNamed(context, route);
        }
      },
      items: items,
    );
  }
}

