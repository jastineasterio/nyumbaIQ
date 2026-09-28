import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';
import '../models/rental_extension_model.dart';

class ExtensionsProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<RentalExtensionModel> _extensions = [];
  int _totalPages = 0;
  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;
  String? _error;

  ExtensionsProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<RentalExtensionModel> get extensions => _extensions;
  bool get isLoading => _isLoading;
  bool get hasMore => _hasMore;
  String? get error => _error;

  Future<void> fetchExtensions({int page = 0, int size = 20, bool reset = true}) async {
    if (_isLoading) return;

    _isLoading = true;
    if (reset) {
      _extensions = [];
      _currentPage = 0;
      _hasMore = true;
      _error = null;
    }
    notifyListeners();

    try {
      final queryParams = <String, dynamic>{'page': page, 'size': size};
      final response = await apiClient.getExtensions(queryParameters: queryParams);

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        final totalPages = json['totalPages'] as int? ?? 0;

        final extensions = content.map((e) => RentalExtensionModel.fromJson(e as Map<String, dynamic>)).toList();

        if (reset) {
          _extensions = extensions;
        } else {
          _extensions.addAll(extensions);
        }
        _totalPages = totalPages;
        _currentPage = page;
        _hasMore = _currentPage < _totalPages - 1;
      } else {
        _error = 'Failed to load extensions';
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

  Future<RentalExtensionModel?> getExtensionById(String id) async {
    try {
      final response = await apiClient.getExtension(id);
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        return RentalExtensionModel.fromJson(json);
      }
      return null;
    } catch (e) {
      if (kDebugMode) print('Get extension error: $e');
      return null;
    }
  }

  Future<bool> createExtension(Map<String, dynamic> data) async {
    try {
      final response = await apiClient.createExtension(data);
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchExtensions(reset: true);
        return true;
      }
      _error = 'Failed to create extension';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> approveExtension(String id) async {
    try {
      final response = await apiClient.approveExtension(id);
      if (response.statusCode == 200) {
        await fetchExtensions(reset: true);
        return true;
      }
      _error = 'Failed to approve extension';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> rejectExtension(String id) async {
    try {
      final response = await apiClient.rejectExtension(id);
      if (response.statusCode == 200) {
        await fetchExtensions(reset: true);
        return true;
      }
      _error = 'Failed to reject extension';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
