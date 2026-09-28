import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';

class TenantsProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<dynamic> _tenants = [];
  int _totalPages = 0;
  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;
  String? _error;

  TenantsProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<dynamic> get tenants => _tenants;
  bool get isLoading => _isLoading;
  bool get hasMore => _hasMore;
  String? get error => _error;

  Future<void> fetchTenants({int page = 0, int size = 20, bool reset = true}) async {
    if (_isLoading) return;

    _isLoading = true;
    if (reset) {
      _tenants = [];
      _currentPage = 0;
      _hasMore = true;
      _error = null;
    }
    notifyListeners();

    try {
      final queryParams = <String, dynamic>{
        'page': page,
        'size': size,
      };

      final response = await apiClient.get(
        ApiEndpoints.tenants,
        queryParameters: queryParams,
      );

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        final totalPages = json['totalPages'] as int? ?? 0;

        if (reset) {
          _tenants = content;
        } else {
          _tenants.addAll(content);
        }
        _totalPages = totalPages;
        _currentPage = page;
        _hasMore = _currentPage < _totalPages - 1;
      } else {
        _error = 'Failed to load tenants';
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

  Future<bool> createTenant(Map<String, dynamic> data) async {
    try {
      final response = await apiClient.post(ApiEndpoints.tenants, body: data);
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchTenants(reset: true);
        return true;
      }
      _error = 'Failed to create tenant';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> updateTenant(String id, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.put('${ApiEndpoints.tenants}/$id', body: data);
      if (response.statusCode == 200) {
        await fetchTenants(reset: true);
        return true;
      }
      _error = 'Failed to update tenant';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> deleteTenant(String id) async {
    try {
      final response = await apiClient.delete('${ApiEndpoints.tenants}/$id');
      if (response.statusCode == 204 || response.statusCode == 200) {
        await fetchTenants(reset: true);
        return true;
      }
      _error = 'Failed to delete tenant';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
