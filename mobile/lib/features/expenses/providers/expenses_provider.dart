import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';
import '../models/expense_model.dart';

class ExpensesProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<ExpenseModel> _expenses = [];
  int _totalPages = 0;
  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;
  String? _error;

  ExpensesProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<ExpenseModel> get expenses => _expenses;
  bool get isLoading => _isLoading;
  bool get hasMore => _hasMore;
  String? get error => _error;

  Future<void> fetchExpenses({int page = 0, int size = 20, bool reset = true}) async {
    if (_isLoading) return;

    _isLoading = true;
    if (reset) {
      _expenses = [];
      _currentPage = 0;
      _hasMore = true;
      _error = null;
    }
    notifyListeners();

    try {
      final queryParams = <String, dynamic>{'page': page, 'size': size};
      final response = await apiClient.getExpenses(queryParameters: queryParams);

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        final totalPages = json['totalPages'] as int? ?? 0;

        final expenses = content.map((e) => ExpenseModel.fromJson(e as Map<String, dynamic>)).toList();

        if (reset) {
          _expenses = expenses;
        } else {
          _expenses.addAll(expenses);
        }
        _totalPages = totalPages;
        _currentPage = page;
        _hasMore = _currentPage < _totalPages - 1;
      } else {
        _error = 'Failed to load expenses';
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

  Future<ExpenseModel?> getExpenseById(String id) async {
    try {
      final response = await apiClient.getExpense(id);
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        return ExpenseModel.fromJson(json);
      }
      return null;
    } catch (e) {
      if (kDebugMode) print('Get expense error: $e');
      return null;
    }
  }

  Future<bool> createExpense(Map<String, dynamic> data) async {
    try {
      final response = await apiClient.createExpense(data);
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchExpenses(reset: true);
        return true;
      }
      _error = 'Failed to create expense';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> updateExpense(String id, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.updateExpense(id, data);
      if (response.statusCode == 200) {
        await fetchExpenses(reset: true);
        return true;
      }
      _error = 'Failed to update expense';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> submitExpense(String id) async {
    try {
      final response = await apiClient.submitExpense(id);
      if (response.statusCode == 200) {
        await fetchExpenses(reset: true);
        return true;
      }
      _error = 'Failed to submit expense';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> approveExpense(String id) async {
    try {
      final response = await apiClient.approveExpense(id);
      if (response.statusCode == 200) {
        await fetchExpenses(reset: true);
        return true;
      }
      _error = 'Failed to approve expense';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> payExpense(String id) async {
    try {
      final response = await apiClient.payExpense(id);
      if (response.statusCode == 200) {
        await fetchExpenses(reset: true);
        return true;
      }
      _error = 'Failed to mark expense as paid';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
