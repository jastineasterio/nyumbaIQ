import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';

class UnitsProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<dynamic> _units = [];
  int _totalPages = 0;
  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;
  String? _error;

  UnitsProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<dynamic> get units => _units;
  bool get isLoading => _isLoading;
  bool get hasMore => _hasMore;
  String? get error => _error;

  Future<void> fetchUnits({
    String? propertyId,
    String? status,
    int page = 0,
    int size = 20,
    bool reset = true,
  }) async {
    if (_isLoading) return;

    _isLoading = true;
    if (reset) {
      _units = [];
      _currentPage = 0;
      _hasMore = true;
      _error = null;
    }
    notifyListeners();

    try {
      final queryParams = <String, dynamic>{
        'page': page,
        'size': size,
        if (propertyId != null) 'propertyId': propertyId,
        if (status != null) 'status': status,
      };

      final response = await apiClient.get(
        ApiEndpoints.units,
        queryParameters: queryParams,
      );

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        final totalPages = json['totalPages'] as int? ?? 0;

        if (reset) {
          _units = content;
        } else {
          _units.addAll(content);
        }
        _totalPages = totalPages;
        _currentPage = page;
        _hasMore = _currentPage < _totalPages - 1;
      } else {
        _error = 'Failed to load units';
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

  Future<void> fetchUnitsByFloor(String floorId) async {
    await fetchUnits(propertyId: null, reset: true);
  }

  Future<bool> createUnit(Map<String, dynamic> data) async {
    try {
      final floorId = data['floorId'] as String;
      final response = await apiClient.post(
        '${ApiEndpoints.floors}/$floorId${ApiEndpoints.units}',
        body: data,
      );
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchUnits(reset: true);
        return true;
      }
      _error = 'Failed to create unit';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> updateUnit(String id, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.put('${ApiEndpoints.units}/$id', body: data);
      if (response.statusCode == 200) {
        await fetchUnits(reset: true);
        return true;
      }
      _error = 'Failed to update unit';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> deleteUnit(String id) async {
    try {
      final response = await apiClient.delete('${ApiEndpoints.units}/$id');
      if (response.statusCode == 204 || response.statusCode == 200) {
        await fetchUnits(reset: true);
        return true;
      }
      _error = 'Failed to delete unit';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
