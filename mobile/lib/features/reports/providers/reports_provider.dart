import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';
import '../models/financial_report_model.dart';

class ReportsProvider extends ChangeNotifier {
  final ApiClient apiClient;
  FinancialReportModel? _incomeReport;
  FinancialReportModel? _expenseReport;
  FinancialReportModel? _outstandingReport;
  bool _isLoading = false;
  String? _error;

  ReportsProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  FinancialReportModel? get incomeReport => _incomeReport;
  FinancialReportModel? get expenseReport => _expenseReport;
  FinancialReportModel? get outstandingReport => _outstandingReport;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> fetchIncomeSummary() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await apiClient.getIncomeSummaryReport();
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        _incomeReport = FinancialReportModel.fromJson(json);
      } else {
        _error = 'Failed to load income summary';
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

  Future<void> fetchExpenseSummary() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await apiClient.getExpenseSummaryReport();
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        _expenseReport = FinancialReportModel.fromJson(json);
      } else {
        _error = 'Failed to load expense summary';
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

  Future<void> fetchOutstandingRent() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await apiClient.getOutstandingRentReport();
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        _outstandingReport = FinancialReportModel.fromJson(json);
      } else {
        _error = 'Failed to load outstanding rent report';
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

  Future<void> refreshAll() async {
    await Future.wait([
      fetchIncomeSummary(),
      fetchExpenseSummary(),
      fetchOutstandingRent(),
    ]);
  }
}
