import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';
import '../models/maintenance_request_model.dart';

class MaintenanceProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<MaintenanceRequestModel> _requests = [];
  int _totalPages = 0;
  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;
  String? _error;

  MaintenanceProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<MaintenanceRequestModel> get requests => _requests;
  bool get isLoading => _isLoading;
  bool get hasMore => _hasMore;
  String? get error => _error;

  Future<void> fetchRequests({int page = 0, int size = 20, bool reset = true}) async {
    if (_isLoading) return;

    _isLoading = true;
    if (reset) {
      _requests = [];
      _currentPage = 0;
      _hasMore = true;
      _error = null;
    }
    notifyListeners();

    try {
      final queryParams = <String, dynamic>{'page': page, 'size': size};
      final response = await apiClient.getMaintenanceRequests(queryParameters: queryParams);

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        final totalPages = json['totalPages'] as int? ?? 0;

        final requests = content.map((e) => MaintenanceRequestModel.fromJson(e as Map<String, dynamic>)).toList();

        if (reset) {
          _requests = requests;
        } else {
          _requests.addAll(requests);
        }
        _totalPages = totalPages;
        _currentPage = page;
        _hasMore = _currentPage < _totalPages - 1;
      } else {
        _error = 'Failed to load maintenance requests';
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

  Future<MaintenanceRequestModel?> getRequestById(String id) async {
    try {
      final response = await apiClient.getMaintenanceRequest(id);
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        return MaintenanceRequestModel.fromJson(json);
      }
      return null;
    } catch (e) {
      if (kDebugMode) print('Get maintenance request error: $e');
      return null;
    }
  }

  Future<bool> createRequest(Map<String, dynamic> data) async {
    try {
      final response = await apiClient.createMaintenance(data);
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchRequests(reset: true);
        return true;
      }
      _error = 'Failed to create maintenance request';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> updateRequest(String id, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.updateMaintenance(id, data);
      if (response.statusCode == 200) {
        await fetchRequests(reset: true);
        return true;
      }
      _error = 'Failed to update maintenance request';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> assignRequest(String id, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.assignMaintenance(id, data);
      if (response.statusCode == 200) {
        await fetchRequests(reset: true);
        return true;
      }
      _error = 'Failed to assign maintenance request';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> updateStatus(String id, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.updateMaintenanceStatus(id, data);
      if (response.statusCode == 200) {
        await fetchRequests(reset: true);
        return true;
      }
      _error = 'Failed to update maintenance status';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> addCosts(String id, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.addMaintenanceCosts(id, data);
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchRequests(reset: true);
        return true;
      }
      _error = 'Failed to add maintenance costs';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
