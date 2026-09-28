import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';

class DashboardProvider extends ChangeNotifier {
  final ApiClient apiClient;
  Map<String, dynamic>? _stats;
  bool _isLoading = false;
  String? _error;

  DashboardProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
    fetchStats();
  }

  Map<String, dynamic>? get stats => _stats;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> fetchStats() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await apiClient.get(ApiEndpoints.dashboardStats);
      if (response.statusCode == 200) {
        _stats = jsonDecode(response.body) as Map<String, dynamic>;
      } else {
        _error = 'Failed to load dashboard stats';
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
}

