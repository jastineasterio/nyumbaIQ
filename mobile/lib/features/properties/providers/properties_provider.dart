import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';

class PropertiesProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<dynamic> _properties = [];
  int _totalPages = 0;
  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;
  String? _error;

  PropertiesProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<dynamic> get properties => _properties;
  bool get isLoading => _isLoading;
  bool get hasMore => _hasMore;
  String? get error => _error;

  Future<void> fetchProperties({int page = 0, int size = 20, bool reset = true}) async {
    if (_isLoading) return;

    _isLoading = true;
    if (reset) {
      _properties = [];
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
        ApiEndpoints.properties,
        queryParameters: queryParams,
      );

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        final totalPages = json['totalPages'] as int? ?? 0;

        if (reset) {
          _properties = content;
        } else {
          _properties.addAll(content);
        }
        _totalPages = totalPages;
        _currentPage = page;
        _hasMore = _currentPage < _totalPages - 1;
      } else {
        _error = 'Failed to load properties';
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

  Future<bool> createProperty(Map<String, dynamic> data) async {
    try {
      final response = await apiClient.post(ApiEndpoints.properties, body: data);
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchProperties(reset: true);
        return true;
      }
      _error = 'Failed to create property';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> updateProperty(String id, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.put('${ApiEndpoints.properties}/$id', body: data);
      if (response.statusCode == 200) {
        await fetchProperties(reset: true);
        return true;
      }
      _error = 'Failed to update property';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> deleteProperty(String id) async {
    try {
      final response = await apiClient.delete('${ApiEndpoints.properties}/$id');
      if (response.statusCode == 204 || response.statusCode == 200) {
        await fetchProperties(reset: true);
        return true;
      }
      _error = 'Failed to delete property';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
