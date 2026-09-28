import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../auth/screens/login_screen.dart';
import '../auth/screens/change_password_screen.dart';
import '../features/dashboard/screens/dashboard_screen.dart';
import '../features/properties/screens/properties_list_screen.dart';
import '../features/properties/screens/property_detail_screen.dart';
import '../features/buildings/screens/buildings_list_screen.dart';
import '../features/buildings/screens/building_form_screen.dart';
import '../features/floors/screens/floors_list_screen.dart';
import '../features/floors/screens/floor_form_screen.dart';
import '../features/units/screens/units_list_screen.dart';
import '../features/units/screens/unit_form_screen.dart';
import '../features/tenants/screens/tenants_list_screen.dart';
import '../features/tenants/screens/tenant_form_screen.dart';
import '../features/tenants/screens/tenant_detail_screen.dart';
import '../features/managers/screens/managers_list_screen.dart';
import '../features/managers/screens/manager_form_screen.dart';
import '../features/kyc/screens/kyc_profile_screen.dart';
import '../features/kyc/screens/kyc_documents_screen.dart';
import '../features/kyc/screens/kyc_upload_screen.dart';
import '../features/audit/screens/audit_logs_screen.dart';
import '../features/profile/screens/profile_screen.dart';
import '../features/leases/screens/leases_list_screen.dart';
import '../features/leases/screens/lease_detail_screen.dart';
import '../features/billing/screens/billing_screen.dart';
import '../features/invoices/screens/invoices_list_screen.dart';
import '../features/invoices/screens/invoice_detail_screen.dart';
import '../features/payments/screens/payments_list_screen.dart';
import '../features/payments/screens/payment_detail_screen.dart';
import '../features/receipts/screens/receipts_list_screen.dart';
import '../features/statements/screens/statements_list_screen.dart';
import '../features/statements/screens/statement_detail_screen.dart';
import '../features/expenses/screens/expenses_list_screen.dart';
import '../features/expenses/screens/expense_form_screen.dart';
import '../features/maintenance/screens/maintenance_list_screen.dart';
import '../features/maintenance/screens/maintenance_form_screen.dart';
import '../features/maintenance/screens/maintenance_detail_screen.dart';
import '../features/extensions/screens/extensions_list_screen.dart';
import '../features/extensions/screens/extension_form_screen.dart';
import '../features/smart_access/screens/smart_access_screen.dart';
import '../features/smart_access/screens/lock_detail_screen.dart';
import '../features/notifications/screens/notifications_screen.dart';
import '../features/reports/screens/reports_screen.dart';
import '../features/reports/screens/financial_summary_screen.dart';
import '../features/ai/screens/ai_assistant_screen.dart';

class AppRoutes {
  static const String login = '/login';
  static const String dashboard = '/dashboard';
  static const String properties = '/properties';
  static const String propertyDetail = '/properties/:id';
  static const String buildings = '/buildings';
  static const String buildingForm = '/buildings/form';
  static const String floors = '/floors';
  static const String floorForm = '/floors/form';
  static const String units = '/units';
  static const String unitForm = '/units/form';
  static const String tenants = '/tenants';
  static const String tenantForm = '/tenants/form';
  static const String tenantDetail = '/tenants/:id';
  static const String managers = '/managers';
  static const String managerForm = '/managers/form';
  static const String kyc = '/kyc';
  static const String kycDocuments = '/kyc/documents';
  static const String kycUpload = '/kyc/upload';
  static const String auditLogs = '/audit-logs';
  static const String profile = '/profile';
  static const String changePassword = '/change-password';
  static const String leases = '/leases';
  static const String leaseDetail = '/leases/:id';
  static const String billing = '/billing';
  static const String invoices = '/invoices';
  static const String invoiceDetail = '/invoices/:id';
  static const String payments = '/payments';
  static const String paymentDetail = '/payments/:id';
  static const String receipts = '/receipts';
  static const String statements = '/statements';
  static const String statementDetail = '/statements/:id';
  static const String expenses = '/expenses';
  static const String expenseForm = '/expenses/form';
  static const String maintenance = '/maintenance';
  static const String maintenanceForm = '/maintenance/form';
  static const String maintenanceDetail = '/maintenance/:id';
  static const String extensions = '/extensions';
  static const String extensionForm = '/extensions/form';
  static const String smartAccess = '/smart-access';
  static const String smartAccessDetail = '/smart-access/detail';
  static const String notifications = '/notifications';
  static const String reports = '/reports';
  static const String financialSummary = '/reports/summary';

  static GoRouter createRouter(bool isAuthenticated) {
    return GoRouter(
      initialLocation: isAuthenticated ? AppRoutes.dashboard : AppRoutes.login,
      redirect: (context, state) {
        final isLoggedIn = isAuthenticated;
        final isLogin = state.matchedLocation == AppRoutes.login;

        if (!isLoggedIn && !isLogin) {
          return AppRoutes.login;
        }
        if (isLoggedIn && isLogin) {
          return AppRoutes.dashboard;
        }
        return null;
      },
      routes: [
        GoRoute(
          path: AppRoutes.login,
          builder: (context, state) => const LoginScreen(),
        ),
        GoRoute(
          path: AppRoutes.changePassword,
          builder: (context, state) => const ChangePasswordScreen(),
        ),
        GoRoute(
          path: AppRoutes.dashboard,
          builder: (context, state) => const DashboardScreen(),
        ),
        GoRoute(
          path: AppRoutes.properties,
          builder: (context, state) => const PropertiesListScreen(),
        ),
        GoRoute(
          path: AppRoutes.propertyDetail,
          builder: (context, state) {
            final id = state.pathParameters['id'] ?? '';
            return PropertyDetailScreen(propertyId: id);
          },
        ),
        GoRoute(
          path: AppRoutes.buildings,
          builder: (context, state) => const BuildingsListScreen(),
        ),
        GoRoute(
          path: AppRoutes.buildingForm,
          builder: (context, state) {
            final propertyId = state.uri.queryParameters['propertyId'];
            final buildingId = state.uri.queryParameters['buildingId'];
            return BuildingFormScreen(
              propertyId: propertyId,
              buildingId: buildingId,
            );
          },
        ),
        GoRoute(
          path: AppRoutes.floors,
          builder: (context, state) => const FloorsListScreen(),
        ),
        GoRoute(
          path: AppRoutes.floorForm,
          builder: (context, state) {
            final buildingId = state.uri.queryParameters['buildingId'];
            final floorId = state.uri.queryParameters['floorId'];
            return FloorFormScreen(buildingId: buildingId, floorId: floorId);
          },
        ),
        GoRoute(
          path: AppRoutes.units,
          builder: (context, state) => const UnitsListScreen(),
        ),
        GoRoute(
          path: AppRoutes.unitForm,
          builder: (context, state) {
            final floorId = state.uri.queryParameters['floorId'];
            final unitId = state.uri.queryParameters['unitId'];
            return UnitFormScreen(floorId: floorId, unitId: unitId);
          },
        ),
        GoRoute(
          path: AppRoutes.tenants,
          builder: (context, state) => const TenantsListScreen(),
        ),
        GoRoute(
          path: AppRoutes.tenantForm,
          builder: (context, state) {
            final unitId = state.uri.queryParameters['unitId'];
            final tenantId = state.uri.queryParameters['tenantId'];
            return TenantFormScreen(unitId: unitId, tenantId: tenantId);
          },
        ),
        GoRoute(
          path: AppRoutes.tenantDetail,
          builder: (context, state) {
            final id = state.pathParameters['id'] ?? '';
            return TenantDetailScreen(tenantId: id);
          },
        ),
        GoRoute(
          path: AppRoutes.managers,
          builder: (context, state) => const ManagersListScreen(),
        ),
        GoRoute(
          path: AppRoutes.managerForm,
          builder: (context, state) {
            final managerId = state.uri.queryParameters['managerId'];
            return ManagerFormScreen(managerId: managerId);
          },
        ),
        GoRoute(
          path: AppRoutes.kyc,
          builder: (context, state) => const KycProfileScreen(),
        ),
        GoRoute(
          path: AppRoutes.kycDocuments,
          builder: (context, state) => const KycDocumentsScreen(),
        ),
        GoRoute(
          path: AppRoutes.kycUpload,
          builder: (context, state) {
            final docType = state.uri.queryParameters['docType'] ?? 'id';
            return KycUploadScreen(documentType: docType);
          },
        ),
        GoRoute(
          path: AppRoutes.auditLogs,
          builder: (context, state) => const AuditLogsScreen(),
        ),
        GoRoute(
          path: AppRoutes.profile,
          builder: (context, state) => const ProfileScreen(),
        ),
        GoRoute(
          path: AppRoutes.leases,
          builder: (context, state) => const LeasesListScreen(),
        ),
        GoRoute(
          path: AppRoutes.leaseDetail,
          builder: (context, state) {
            final id = state.pathParameters['id'] ?? '';
            return LeaseDetailScreen(leaseId: id);
          },
        ),
        GoRoute(
          path: AppRoutes.billing,
          builder: (context, state) => const BillingScreen(),
        ),
        GoRoute(
          path: AppRoutes.invoices,
          builder: (context, state) => const InvoicesListScreen(),
        ),
        GoRoute(
          path: AppRoutes.invoiceDetail,
          builder: (context, state) {
            final id = state.pathParameters['id'] ?? '';
            return InvoiceDetailScreen(invoiceId: id);
          },
        ),
        GoRoute(
          path: AppRoutes.payments,
          builder: (context, state) => const PaymentsListScreen(),
        ),
        GoRoute(
          path: AppRoutes.paymentDetail,
          builder: (context, state) {
            final id = state.pathParameters['id'] ?? '';
            return PaymentDetailScreen(paymentId: id);
          },
        ),
        GoRoute(
          path: AppRoutes.receipts,
          builder: (context, state) => const ReceiptsListScreen(),
        ),
        GoRoute(
          path: AppRoutes.statements,
          builder: (context, state) => const StatementsListScreen(),
        ),
        GoRoute(
          path: AppRoutes.statementDetail,
          builder: (context, state) {
            final id = state.pathParameters['id'] ?? '';
            return StatementDetailScreen(statementId: id);
          },
        ),
        GoRoute(
          path: AppRoutes.expenses,
          builder: (context, state) => const ExpensesListScreen(),
        ),
        GoRoute(
          path: AppRoutes.expenseForm,
          builder: (context, state) => const ExpenseFormScreen(),
        ),
        GoRoute(
          path: AppRoutes.maintenance,
          builder: (context, state) => const MaintenanceListScreen(),
        ),
        GoRoute(
          path: AppRoutes.maintenanceForm,
          builder: (context, state) => const MaintenanceFormScreen(),
        ),
        GoRoute(
          path: AppRoutes.maintenanceDetail,
          builder: (context, state) {
            final id = state.pathParameters['id'] ?? '';
            return MaintenanceDetailScreen(requestId: id);
          },
        ),
        GoRoute(
          path: AppRoutes.extensions,
          builder: (context, state) => const ExtensionsListScreen(),
        ),
        GoRoute(
          path: AppRoutes.extensionForm,
          builder: (context, state) => const ExtensionFormScreen(),
        ),
        GoRoute(
          path: AppRoutes.smartAccess,
          builder: (context, state) => const SmartAccessScreen(),
        ),
        GoRoute(
          path: AppRoutes.smartAccessDetail,
          builder: (context, state) {
            final id = state.pathParameters['id'] ?? '';
            return LockDetailScreen(lockId: id);
          },
        ),
        GoRoute(
          path: AppRoutes.notifications,
          builder: (context, state) => const NotificationsScreen(),
        ),
        GoRoute(
          path: AppRoutes.reports,
          builder: (context, state) => const ReportsScreen(),
        ),
        GoRoute(
          path: AppRoutes.financialSummary,
          builder: (context, state) => const FinancialSummaryScreen(),
        ),
        GoRoute(
          path: '/ai-assistant',
          builder: (context, state) => const AiAssistantScreen(),
        ),
      ],
    );
  }
}

