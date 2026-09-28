import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';
import '../models/smart_lock_model.dart';

class SmartAccessProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<SmartLockModel> _locks = [];
  bool _isLoading = false;
  String? _error;

  SmartAccessProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<SmartLockModel> get locks => _locks;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> fetchLocks() async {
    if (_isLoading) return;

    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await apiClient.getSmartLocks();
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        _locks = content.map((e) => SmartLockModel.fromJson(e as Map<String, dynamic>)).toList();
      } else {
        _error = 'Failed to load smart locks';
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

  Future<SmartLockModel?> getLockById(String id) async {
    try {
      final response = await apiClient.getSmartLock(id);
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        return SmartLockModel.fromJson(json);
      }
      return null;
    } catch (e) {
      if (kDebugMode) print('Get smart lock error: $e');
      return null;
    }
  }

  Future<bool> createLock(Map<String, dynamic> data) async {
    try {
      final response = await apiClient.createSmartLock(data);
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchLocks();
        return true;
      }
      _error = 'Failed to create smart lock';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> lock(String id) async {
    try {
      final response = await apiClient.lockSmartLock(id);
      if (response.statusCode == 200) {
        await fetchLocks();
        return true;
      }
      _error = 'Failed to lock';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> unlock(String id) async {
    try {
      final response = await apiClient.unlockSmartLock(id);
      if (response.statusCode == 200) {
        await fetchLocks();
        return true;
      }
      _error = 'Failed to unlock';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> setCredentials(String id, Map<String, dynamic> data) async {
    try {
      final response = await apiClient.setSmartLockCredentials(id, data);
      if (response.statusCode == 200 || response.statusCode == 201) {
        return true;
      }
      _error = 'Failed to set credentials';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<List<AccessEventModel>> getEvents(String id) async {
    try {
      final response = await apiClient.getSmartLockEvents(id);
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        return content.map((e) => AccessEventModel.fromJson(e as Map<String, dynamic>)).toList();
      }
      return [];
    } catch (e) {
      if (kDebugMode) print('Get lock events error: $e');
      return [];
    }
  }

  Future<List<AccessCredentialModel>> getAccessPolicy(String id) async {
    try {
      final response = await apiClient.getSmartLockAccessPolicy(id);
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        return content.map((e) => AccessCredentialModel.fromJson(e as Map<String, dynamic>)).toList();
      }
      return [];
    } catch (e) {
      if (kDebugMode) print('Get access policy error: $e');
      return [];
    }
  }
}
