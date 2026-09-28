import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../auth/providers/auth_provider.dart';
import '../auth/services/auth_service.dart';
import '../core/network/api_client.dart';
import '../core/storage/local_storage.dart';
import '../core/storage/secure_storage.dart';
import '../features/dashboard/providers/dashboard_provider.dart';
import '../features/properties/providers/properties_provider.dart';
import '../features/buildings/providers/buildings_provider.dart';
import '../features/floors/providers/floors_provider.dart';
import '../features/units/providers/units_provider.dart';
import '../features/tenants/providers/tenants_provider.dart';
import '../features/managers/providers/managers_provider.dart';
import '../features/kyc/providers/kyc_provider.dart';
import '../features/audit/providers/audit_provider.dart';
import '../features/profile/providers/profile_provider.dart';
import '../features/leases/providers/leases_provider.dart';
import '../features/billing/providers/billing_provider.dart';
import '../features/invoices/providers/invoices_provider.dart';
import '../features/payments/providers/payments_provider.dart';
import '../features/expenses/providers/expenses_provider.dart';
import '../features/maintenance/providers/maintenance_provider.dart';
import '../features/extensions/providers/extensions_provider.dart';
import '../features/smart_access/providers/smart_access_provider.dart';
import '../features/notifications/providers/notifications_provider.dart';
import '../features/reports/providers/reports_provider.dart';
import '../features/ai/providers/ai_provider.dart';

class DependencyInjection {
  static Future<void> setupProviders(BuildContext context) async {
    final prefs = await SharedPreferences.getInstance();
    final secureStorage = const FlutterSecureStorage();
    final apiClient = ApiClient(SecureStorageService(secureStorage));
    final localStorage = LocalStorageService(prefs);
    final secureStorageService = SecureStorageService(secureStorage);
    final authService = AuthService(apiClient, secureStorageService, localStorage);

    Provider.of<AuthProvider>(context, listen: false).initialize(authService);
    Provider.of<DashboardProvider>(context, listen: false).initialize(apiClient);
    Provider.of<PropertiesProvider>(context, listen: false).initialize(apiClient);
    Provider.of<BuildingsProvider>(context, listen: false).initialize(apiClient);
    Provider.of<FloorsProvider>(context, listen: false).initialize(apiClient);
    Provider.of<UnitsProvider>(context, listen: false).initialize(apiClient);
    Provider.of<TenantsProvider>(context, listen: false).initialize(apiClient);
    Provider.of<ManagersProvider>(context, listen: false).initialize(apiClient);
    Provider.of<KycProvider>(context, listen: false).initialize(apiClient);
    Provider.of<AuditProvider>(context, listen: false).initialize(apiClient);
    Provider.of<ProfileProvider>(context, listen: false);
    Provider.of<LeasesProvider>(context, listen: false).initialize(apiClient);
    Provider.of<BillingProvider>(context, listen: false).initialize(apiClient);
    Provider.of<InvoicesProvider>(context, listen: false).initialize(apiClient);
    Provider.of<PaymentsProvider>(context, listen: false).initialize(apiClient);
    Provider.of<ExpensesProvider>(context, listen: false).initialize(apiClient);
    Provider.of<MaintenanceProvider>(context, listen: false).initialize(apiClient);
    Provider.of<ExtensionsProvider>(context, listen: false).initialize(apiClient);
    Provider.of<SmartAccessProvider>(context, listen: false).initialize(apiClient);
    Provider.of<NotificationsProvider>(context, listen: false).initialize(apiClient);
    Provider.of<ReportsProvider>(context, listen: false).initialize(apiClient);
    Provider.of<AiProvider>(context, listen: false);
  }
}
