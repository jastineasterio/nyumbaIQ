import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';
import '../models/invoice_model.dart';

class InvoicesProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<InvoiceModel> _invoices = [];
  int _totalPages = 0;
  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;
  String? _error;

  InvoicesProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<InvoiceModel> get invoices => _invoices;
  bool get isLoading => _isLoading;
  bool get hasMore => _hasMore;
  String? get error => _error;

  Future<void> fetchInvoices({int page = 0, int size = 20, bool reset = true}) async {
    if (_isLoading) return;

    _isLoading = true;
    if (reset) {
      _invoices = [];
      _currentPage = 0;
      _hasMore = true;
      _error = null;
    }
    notifyListeners();

    try {
      final queryParams = <String, dynamic>{'page': page, 'size': size};
      final response = await apiClient.getInvoices(queryParameters: queryParams);

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        final totalPages = json['totalPages'] as int? ?? 0;

        final invoices = content.map((e) => InvoiceModel.fromJson(e as Map<String, dynamic>)).toList();

        if (reset) {
          _invoices = invoices;
        } else {
          _invoices.addAll(invoices);
        }
        _totalPages = totalPages;
        _currentPage = page;
        _hasMore = _currentPage < _totalPages - 1;
      } else {
        _error = 'Failed to load invoices';
      }
    } on ServerException catch (e) {
      _error = e.message;
    } on NetworkException catch (e) {
      _error = e.message;
    } catch (e) {
      _error = 'An unexpected error occurred';
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<InvoiceModel?> getInvoiceById(String id) async {
    try {
      final response = await apiClient.getInvoice(id);
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        return InvoiceModel.fromJson(json);
      }
      return null;
    } catch (e) {
      if (kDebugMode) print('Get invoice error: $e');
      return null;
    }
  }

  Future<bool> cancelInvoice(String id) async {
    try {
      final response = await apiClient.cancelInvoice(id);
      if (response.statusCode == 200) {
        await fetchInvoices(reset: true);
        return true;
      }
      _error = 'Failed to cancel invoice';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
