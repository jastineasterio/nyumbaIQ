import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'config/routes.dart';
import 'auth/providers/auth_provider.dart';
import 'auth/services/auth_service.dart';
import 'core/constants/api_constants.dart';
import 'core/network/api_client.dart';
import 'core/storage/secure_storage.dart';
import 'core/storage/local_storage.dart';
import 'core/theme/app_theme.dart';
import 'features/dashboard/providers/dashboard_provider.dart';
import 'features/properties/providers/properties_provider.dart';
import 'features/buildings/providers/buildings_provider.dart';
import 'features/floors/providers/floors_provider.dart';
import 'features/units/providers/units_provider.dart';
import 'features/tenants/providers/tenants_provider.dart';
import 'features/managers/providers/managers_provider.dart';
import 'features/kyc/providers/kyc_provider.dart';
import 'features/audit/providers/audit_provider.dart';
import 'features/profile/providers/profile_provider.dart';
import 'features/leases/providers/leases_provider.dart';
import 'features/billing/providers/billing_provider.dart';
import 'features/invoices/providers/invoices_provider.dart';
import 'features/payments/providers/payments_provider.dart';
import 'features/expenses/providers/expenses_provider.dart';
import 'features/maintenance/providers/maintenance_provider.dart';
import 'features/extensions/providers/extensions_provider.dart';
import 'features/smart_access/providers/smart_access_provider.dart';
import 'features/notifications/providers/notifications_provider.dart';
import 'features/reports/providers/reports_provider.dart';
import 'features/ai/providers/ai_provider.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  final prefs = await SharedPreferences.getInstance();
  final secureStorage = const FlutterSecureStorage();
  final localStorage = LocalStorageService(prefs);
  final secureStorageService = SecureStorageService(secureStorage);
  final apiClient = ApiClient(secureStorageService);
  final authService = AuthService(apiClient, secureStorageService, localStorage);

  final token = await secureStorage.read(key: AppConstants.tokenKey);
  final isAuthenticated = token != null;

  runApp(MyApp(
    apiClient: apiClient,
    authService: authService,
    localStorage: localStorage,
    isAuthenticated: isAuthenticated,
  ));
}

class MyApp extends StatelessWidget {
  final ApiClient apiClient;
  final AuthService authService;
  final LocalStorageService localStorage;
  final bool isAuthenticated;

  const MyApp({
    super.key,
    required this.apiClient,
    required this.authService,
    required this.localStorage,
    required this.isAuthenticated,
  });

  @override
  Widget build(BuildContext context) {
    final router = AppRoutes.createRouter(isAuthenticated);

    return MultiProvider(
      providers: [
        ChangeNotifierProvider(
          create: (_) => AuthProvider(
            authService: authService,
            secureStorage: SecureStorageService(const FlutterSecureStorage()),
            localStorage: localStorage,
          ),
        ),
        ChangeNotifierProvider(
          create: (_) => DashboardProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => PropertiesProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => BuildingsProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => FloorsProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => UnitsProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => TenantsProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => ManagersProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => KycProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => AuditProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => ProfileProvider(apiClient, localStorage),
        ),
        ChangeNotifierProvider(
          create: (_) => LeasesProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => BillingProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => InvoicesProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => PaymentsProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => ExpensesProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => MaintenanceProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => ExtensionsProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => SmartAccessProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => NotificationsProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => ReportsProvider()..initialize(apiClient),
        ),
        ChangeNotifierProvider(
          create: (_) => AiProvider(apiClient: apiClient),
        ),
      ],
      child: MaterialApp.router(
        title: AppConstants.appName,
        debugShowCheckedModeBanner: false,
        theme: AppTheme.lightTheme,
        routerConfig: router,
      ),
    );
  }
}
