import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import '../constants/api_constants.dart';
import '../errors/exceptions.dart';
import '../storage/secure_storage.dart';
import '../network/api_endpoints.dart';

class ApiClient {
  final SecureStorageService? secureStorage;
  http.Client? _client;

  ApiClient([this.secureStorage]);

  http.Client get client => _client ?? http.Client();

  void updateClient(http.Client newClient) {
    _client?.close();
    _client = newClient;
  }

  Future<Map<String, String>> _getHeaders({bool includeAuth = true}) async {
    final headers = <String, String>{
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };

    if (includeAuth && secureStorage != null) {
      final token = await secureStorage!.getAccessToken();
      if (token != null) {
        headers['Authorization'] = 'Bearer $token';
      }
    }

    return headers;
  }

  Future<http.Response> get(
    String endpoint, {
    Map<String, dynamic>? queryParameters,
    bool includeAuth = true,
  }) async {
    final uri = Uri.parse('${ApiConstants.baseUrl}$endpoint')
        .replace(queryParameters: queryParameters);
    final headers = await _getHeaders(includeAuth: includeAuth);

    try {
      return await client.get(uri, headers: headers);
    } on SocketException catch (e) {
      throw NetworkException('No internet connection: ${e.message}');
    } on HttpException catch (e) {
      throw NetworkException('HTTP error: ${e.message}');
    }
  }

  Future<http.Response> post(
    String endpoint, {
    dynamic body,
    bool includeAuth = true,
  }) async {
    final uri = Uri.parse('${ApiConstants.baseUrl}$endpoint');
    final headers = await _getHeaders(includeAuth: includeAuth);
    final encodedBody = body != null ? jsonEncode(body) : null;

    try {
      return await client.post(uri, headers: headers, body: encodedBody);
    } on SocketException catch (e) {
      throw NetworkException('No internet connection: ${e.message}');
    } on HttpException catch (e) {
      throw NetworkException('HTTP error: ${e.message}');
    }
  }

  Future<http.Response> put(
    String endpoint, {
    dynamic body,
    bool includeAuth = true,
  }) async {
    final uri = Uri.parse('${ApiConstants.baseUrl}$endpoint');
    final headers = await _getHeaders(includeAuth: includeAuth);
    final encodedBody = body != null ? jsonEncode(body) : null;

    try {
      return await client.put(uri, headers: headers, body: encodedBody);
    } on SocketException catch (e) {
      throw NetworkException('No internet connection: ${e.message}');
    } on HttpException catch (e) {
      throw NetworkException('HTTP error: ${e.message}');
    }
  }

  Future<http.Response> delete(
    String endpoint, {
    bool includeAuth = true,
  }) async {
    final uri = Uri.parse('${ApiConstants.baseUrl}$endpoint');
    final headers = await _getHeaders(includeAuth: includeAuth);

    try {
      return await client.delete(uri, headers: headers);
    } on SocketException catch (e) {
      throw NetworkException('No internet connection: ${e.message}');
    } on HttpException catch (e) {
      throw NetworkException('HTTP error: ${e.message}');
    }
  }

  Future<http.StreamedResponse> postMultipart(
    String endpoint, {
    Map<String, String>? fields,
    Map<String, String>? headers,
  }) async {
    final uri = Uri.parse('${ApiConstants.baseUrl}$endpoint');
    final request = http.MultipartRequest('POST', uri);

    if (fields != null) request.fields.addAll(fields);
    if (headers != null) request.headers.addAll(headers);

    if (secureStorage != null) {
      final authToken = await secureStorage!.getAccessToken();
      if (authToken != null) {
        request.headers['Authorization'] = 'Bearer $authToken';
      }
    }

    return await request.send();
  }

  Future<http.Response> createLease(Map<String, dynamic> body) async {
    return await post(ApiEndpoints.leases, body: body);
  }

  Future<http.Response> getLeases({Map<String, dynamic>? queryParameters}) async {
    return await get(ApiEndpoints.leases, queryParameters: queryParameters);
  }

  Future<http.Response> getLease(String id) async {
    return await get('${ApiEndpoints.leases}/$id');
  }

  Future<http.Response> updateLease(String id, Map<String, dynamic> body) async {
    return await put('${ApiEndpoints.leases}/$id', body: body);
  }

  Future<http.Response> renewLease(String id) async {
    return await post('${ApiEndpoints.leases}/$id/renew');
  }

  Future<http.Response> approveLease(String id) async {
    return await post('${ApiEndpoints.leases}/$id/approve');
  }

  Future<http.Response> terminateLease(String id) async {
    return await post('${ApiEndpoints.leases}/$id/terminate');
  }

  Future<http.Response> generateBilling(Map<String, dynamic> body) async {
    return await post(ApiEndpoints.billingGenerate, body: body);
  }

  Future<http.Response> getBillingSchedules({Map<String, dynamic>? queryParameters}) async {
    return await get(ApiEndpoints.billingSchedules, queryParameters: queryParameters);
  }

  Future<http.Response> getBillingSchedule(String id) async {
    return await get('${ApiEndpoints.billingSchedules}/$id');
  }

  Future<http.Response> getInvoices({Map<String, dynamic>? queryParameters}) async {
    return await get(ApiEndpoints.invoices, queryParameters: queryParameters);
  }

  Future<http.Response> getInvoice(String id) async {
    return await get('${ApiEndpoints.invoices}/$id');
  }

  Future<http.Response> getInvoiceByNumber(String invoiceNumber) async {
    return await get('${ApiEndpoints.invoicesByNumber}/$invoiceNumber');
  }

  Future<http.Response> cancelInvoice(String id) async {
    return await put('${ApiEndpoints.invoices}/$id/cancel');
  }

  Future<http.Response> createPayment(Map<String, dynamic> body) async {
    return await post(ApiEndpoints.payments, body: body);
  }

  Future<http.Response> getPayments({Map<String, dynamic>? queryParameters}) async {
    return await get(ApiEndpoints.payments, queryParameters: queryParameters);
  }

  Future<http.Response> getPayment(String id) async {
    return await get('${ApiEndpoints.payments}/$id');
  }

  Future<http.Response> allocatePayment(String id, Map<String, dynamic> body) async {
    return await post('${ApiEndpoints.payments}/$id/allocate', body: body);
  }

  Future<http.Response> getPaymentByReference(String reference) async {
    return await get('${ApiEndpoints.paymentsByReference}/$reference');
  }

  Future<http.Response> getReceipts({Map<String, dynamic>? queryParameters}) async {
    return await get(ApiEndpoints.receipts, queryParameters: queryParameters);
  }

  Future<http.Response> getReceipt(String id) async {
    return await get('${ApiEndpoints.receipts}/$id');
  }

  Future<http.Response> getReceiptByNumber(String receiptNumber) async {
    return await get('${ApiEndpoints.receiptsByNumber}/$receiptNumber');
  }

  Future<http.Response> getStatements({Map<String, dynamic>? queryParameters}) async {
    return await get(ApiEndpoints.statements, queryParameters: queryParameters);
  }

  Future<http.Response> getStatement(String id) async {
    return await get('${ApiEndpoints.statements}/$id');
  }

  Future<http.Response> generateStatement(Map<String, dynamic> body) async {
    return await post(ApiEndpoints.statementsGenerate, body: body);
  }

  Future<http.Response> createExpense(Map<String, dynamic> body) async {
    return await post(ApiEndpoints.expenses, body: body);
  }

  Future<http.Response> getExpenses({Map<String, dynamic>? queryParameters}) async {
    return await get(ApiEndpoints.expenses, queryParameters: queryParameters);
  }

  Future<http.Response> getExpense(String id) async {
    return await get('${ApiEndpoints.expenses}/$id');
  }

  Future<http.Response> updateExpense(String id, Map<String, dynamic> body) async {
    return await put('${ApiEndpoints.expenses}/$id', body: body);
  }

  Future<http.Response> submitExpense(String id) async {
    return await post('${ApiEndpoints.expenses}/$id/submit');
  }

  Future<http.Response> approveExpense(String id) async {
    return await post('${ApiEndpoints.expenses}/$id/approve');
  }

  Future<http.Response> payExpense(String id) async {
    return await post('${ApiEndpoints.expenses}/$id/pay');
  }

  Future<http.Response> createMaintenance(Map<String, dynamic> body) async {
    return await post(ApiEndpoints.maintenance, body: body);
  }

  Future<http.Response> getMaintenanceRequests({Map<String, dynamic>? queryParameters}) async {
    return await get(ApiEndpoints.maintenance, queryParameters: queryParameters);
  }

  Future<http.Response> getMaintenanceRequest(String id) async {
    return await get('${ApiEndpoints.maintenance}/$id');
  }

  Future<http.Response> updateMaintenance(String id, Map<String, dynamic> body) async {
    return await put('${ApiEndpoints.maintenance}/$id', body: body);
  }

  Future<http.Response> assignMaintenance(String id, Map<String, dynamic> body) async {
    return await post('${ApiEndpoints.maintenance}/$id/assign', body: body);
  }

  Future<http.Response> updateMaintenanceStatus(String id, Map<String, dynamic> body) async {
    return await post('${ApiEndpoints.maintenance}/$id/status', body: body);
  }

  Future<http.Response> addMaintenanceCosts(String id, Map<String, dynamic> body) async {
    return await post('${ApiEndpoints.maintenance}/$id/costs', body: body);
  }

  Future<http.Response> getMaintenanceCosts(String id) async {
    return await get('${ApiEndpoints.maintenance}/$id/costs');
  }

  Future<http.Response> createExtension(Map<String, dynamic> body) async {
    return await post(ApiEndpoints.extensions, body: body);
  }

  Future<http.Response> getExtensions({Map<String, dynamic>? queryParameters}) async {
    return await get(ApiEndpoints.extensions, queryParameters: queryParameters);
  }

  Future<http.Response> getExtension(String id) async {
    return await get('${ApiEndpoints.extensions}/$id');
  }

  Future<http.Response> approveExtension(String id) async {
    return await post('${ApiEndpoints.extensions}/$id/approve');
  }

  Future<http.Response> rejectExtension(String id) async {
    return await post('${ApiEndpoints.extensions}/$id/reject');
  }

  Future<http.Response> createSmartLock(Map<String, dynamic> body) async {
    return await post(ApiEndpoints.smartLocks, body: body);
  }

  Future<http.Response> getSmartLocks({Map<String, dynamic>? queryParameters}) async {
    return await get(ApiEndpoints.smartLocks, queryParameters: queryParameters);
  }

  Future<http.Response> getSmartLock(String id) async {
    return await get('${ApiEndpoints.smartLocks}/$id');
  }

  Future<http.Response> lockSmartLock(String id) async {
    return await post('${ApiEndpoints.smartLocks}/$id/lock');
  }

  Future<http.Response> unlockSmartLock(String id) async {
    return await post('${ApiEndpoints.smartLocks}/$id/unlock');
  }

  Future<http.Response> setSmartLockCredentials(String id, Map<String, dynamic> body) async {
    return await post('${ApiEndpoints.smartLocks}/$id/credentials', body: body);
  }

  Future<http.Response> getSmartLockEvents(String id) async {
    return await get('${ApiEndpoints.smartLocks}/$id/events');
  }

  Future<http.Response> getSmartLockAccessPolicy(String id) async {
    return await get('${ApiEndpoints.smartLocks}/$id/access-policy');
  }

  Future<http.Response> getNotifications({Map<String, dynamic>? queryParameters}) async {
    return await get(ApiEndpoints.notifications, queryParameters: queryParameters);
  }

  Future<http.Response> getUnreadNotificationsCount() async {
    return await get(ApiEndpoints.notificationsUnreadCount);
  }

  Future<http.Response> markNotificationAsRead(String id) async {
    return await post('${ApiEndpoints.notifications}/$id/read');
  }

  Future<http.Response> markAllNotificationsAsRead() async {
    return await post(ApiEndpoints.notificationsMarkAllRead);
  }

  Future<http.Response> getIncomeSummaryReport() async {
    return await get(ApiEndpoints.reportsIncomeSummary);
  }

  Future<http.Response> getExpenseSummaryReport() async {
    return await get(ApiEndpoints.reportsExpenseSummary);
  }

  Future<http.Response> getOutstandingRentReport() async {
    return await get(ApiEndpoints.reportsOutstandingRent);
  }
}
