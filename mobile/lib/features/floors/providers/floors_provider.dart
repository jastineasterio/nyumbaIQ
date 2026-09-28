import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';

class FloorsProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<dynamic> _floors = [];
  int _totalPages = 0;
  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;
  String? _error;
  String? _buildingId;

  FloorsProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<dynamic> get floors => _floors;
  bool get isLoading => _isLoading;
  bool get hasMore => _hasMore;
  String? get error => _error;

  Future<void> fetchFloors(String buildingId, {int page = 0, int size = 20, bool reset = true}) async {
    if (_isLoading) return;

    _isLoading = true;
    _buildingId = buildingId;
    if (reset) {
      _floors = [];
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
        '${ApiEndpoints.buildings}/$buildingId${ApiEndpoints.floors}',
        queryParameters: queryParams,
      );

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        final totalPages = json['totalPages'] as int? ?? 0;

        if (reset) {
          _floors = content;
        } else {
          _floors.addAll(content);
        }
        _totalPages = totalPages;
        _currentPage = page;
        _hasMore = _currentPage < _totalPages - 1;
      } else {
        _error = 'Failed to load floors';
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

  Future<bool> createFloor(String buildingId, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.post(
        '${ApiEndpoints.buildings}/$buildingId${ApiEndpoints.floors}',
        body: data,
      );
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchFloors(buildingId, reset: true);
        return true;
      }
      _error = 'Failed to create floor';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> updateFloor(String id, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.put('${ApiEndpoints.floors}/$id', body: data);
      if (response.statusCode == 200) {
        if (_buildingId != null) await fetchFloors(_buildingId!, reset: true);
        return true;
      }
      _error = 'Failed to update floor';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> deleteFloor(String id) async {
    try {
      final response = await apiClient.delete('${ApiEndpoints.floors}/$id');
      if (response.statusCode == 204 || response.statusCode == 200) {
        if (_buildingId != null) await fetchFloors(_buildingId!, reset: true);
        return true;
      }
      _error = 'Failed to delete floor';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
