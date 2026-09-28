import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';
import '../models/lease_model.dart';

class LeasesProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<LeaseModel> _leases = [];
  int _totalPages = 0;
  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;
  String? _error;

  LeasesProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<LeaseModel> get leases => _leases;
  bool get isLoading => _isLoading;
  bool get hasMore => _hasMore;
  String? get error => _error;

  Future<void> fetchLeases({int page = 0, int size = 20, bool reset = true}) async {
    if (_isLoading) return;

    _isLoading = true;
    if (reset) {
      _leases = [];
      _currentPage = 0;
      _hasMore = true;
      _error = null;
    }
    notifyListeners();

    try {
      final queryParams = <String, dynamic>{'page': page, 'size': size};
      final response = await apiClient.get(ApiEndpoints.leases, queryParameters: queryParams);

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        final totalPages = json['totalPages'] as int? ?? 0;

        final leases = content.map((e) => LeaseModel.fromJson(e as Map<String, dynamic>)).toList();

        if (reset) {
          _leases = leases;
        } else {
          _leases.addAll(leases);
        }
        _totalPages = totalPages;
        _currentPage = page;
        _hasMore = _currentPage < _totalPages - 1;
      } else {
        _error = 'Failed to load leases';
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

  Future<bool> createLease(Map<String, dynamic> data) async {
    try {
      final response = await apiClient.createLease(data);
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchLeases(reset: true);
        return true;
      }
      _error = 'Failed to create lease';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> updateLease(String id, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.updateLease(id, data);
      if (response.statusCode == 200) {
        await fetchLeases(reset: true);
        return true;
      }
      _error = 'Failed to update lease';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> renewLease(String id) async {
    try {
      final response = await apiClient.renewLease(id);
      if (response.statusCode == 200) {
        await fetchLeases(reset: true);
        return true;
      }
      _error = 'Failed to renew lease';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> approveLease(String id) async {
    try {
      final response = await apiClient.approveLease(id);
      if (response.statusCode == 200) {
        await fetchLeases(reset: true);
        return true;
      }
      _error = 'Failed to approve lease';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> terminateLease(String id) async {
    try {
      final response = await apiClient.terminateLease(id);
      if (response.statusCode == 200) {
        await fetchLeases(reset: true);
        return true;
      }
      _error = 'Failed to terminate lease';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
