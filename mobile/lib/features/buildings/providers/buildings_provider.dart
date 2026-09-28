import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';

class BuildingsProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<dynamic> _buildings = [];
  int _totalPages = 0;
  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;
  String? _error;
  String? _propertyId;

  BuildingsProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<dynamic> get buildings => _buildings;
  bool get isLoading => _isLoading;
  bool get hasMore => _hasMore;
  String? get error => _error;

  Future<void> fetchBuildings(String propertyId, {int page = 0, int size = 20, bool reset = true}) async {
    if (_isLoading) return;

    _isLoading = true;
    _propertyId = propertyId;
    if (reset) {
      _buildings = [];
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
        '${ApiEndpoints.properties}/$propertyId${ApiEndpoints.buildings}',
        queryParameters: queryParams,
      );

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        final totalPages = json['totalPages'] as int? ?? 0;

        if (reset) {
          _buildings = content;
        } else {
          _buildings.addAll(content);
        }
        _totalPages = totalPages;
        _currentPage = page;
        _hasMore = _currentPage < _totalPages - 1;
      } else {
        _error = 'Failed to load buildings';
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

  Future<bool> createBuilding(String propertyId, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.post(
        '${ApiEndpoints.properties}/$propertyId${ApiEndpoints.buildings}',
        body: data,
      );
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchBuildings(propertyId, reset: true);
        return true;
      }
      _error = 'Failed to create building';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> updateBuilding(String id, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.put('${ApiEndpoints.buildings}/$id', body: data);
      if (response.statusCode == 200) {
        if (_propertyId != null) await fetchBuildings(_propertyId!, reset: true);
        return true;
      }
      _error = 'Failed to update building';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> deleteBuilding(String id) async {
    try {
      final response = await apiClient.delete('${ApiEndpoints.buildings}/$id');
      if (response.statusCode == 204 || response.statusCode == 200) {
        if (_propertyId != null) await fetchBuildings(_propertyId!, reset: true);
        return true;
      }
      _error = 'Failed to delete building';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
