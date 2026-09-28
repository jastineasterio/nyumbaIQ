import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';
import '../models/payment_model.dart';

class PaymentsProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<PaymentModel> _payments = [];
  int _totalPages = 0;
  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;
  String? _error;

  PaymentsProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<PaymentModel> get payments => _payments;
  bool get isLoading => _isLoading;
  bool get hasMore => _hasMore;
  String? get error => _error;

  Future<void> fetchPayments({int page = 0, int size = 20, bool reset = true}) async {
    if (_isLoading) return;

    _isLoading = true;
    if (reset) {
      _payments = [];
      _currentPage = 0;
      _hasMore = true;
      _error = null;
    }
    notifyListeners();

    try {
      final queryParams = <String, dynamic>{'page': page, 'size': size};
      final response = await apiClient.getPayments(queryParameters: queryParams);

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        final totalPages = json['totalPages'] as int? ?? 0;

        final payments = content.map((e) => PaymentModel.fromJson(e as Map<String, dynamic>)).toList();

        if (reset) {
          _payments = payments;
        } else {
          _payments.addAll(payments);
        }
        _totalPages = totalPages;
        _currentPage = page;
        _hasMore = _currentPage < _totalPages - 1;
      } else {
        _error = 'Failed to load payments';
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

  Future<PaymentModel?> getPaymentById(String id) async {
    try {
      final response = await apiClient.getPayment(id);
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        return PaymentModel.fromJson(json);
      }
      return null;
    } catch (e) {
      if (kDebugMode) print('Get payment error: $e');
      return null;
    }
  }

  Future<bool> createPayment(Map<String, dynamic> data) async {
    try {
      final response = await apiClient.createPayment(data);
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchPayments(reset: true);
        return true;
      }
      _error = 'Failed to create payment';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> allocatePayment(String id, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.allocatePayment(id, data);
      if (response.statusCode == 200) {
        await fetchPayments(reset: true);
        return true;
      }
      _error = 'Failed to allocate payment';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
