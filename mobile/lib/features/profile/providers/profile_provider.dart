import 'dart:convert';

import 'package:flutter/foundation.dart';
import '../../../core/storage/local_storage.dart';
import '../../../core/network/api_client.dart';
import '../../../core/errors/exceptions.dart';

class ProfileProvider extends ChangeNotifier {
  final ApiClient apiClient;
  final LocalStorageService localStorage;
  Map<String, dynamic>? _profile;
  bool _isLoading = false;
  String? _error;

  ProfileProvider(this.apiClient, this.localStorage);

  Map<String, dynamic>? get profile => _profile;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> fetchProfile() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await apiClient.get('/users/me');
      if (response.statusCode == 200) {
        _profile = jsonDecode(response.body) as Map<String, dynamic>;
      } else {
        _error = 'Failed to load profile';
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

  Future<bool> updateProfile(Map<String, dynamic> data) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await apiClient.put('/users/me', body: data);
      if (response.statusCode == 200) {
        _profile = jsonDecode(response.body) as Map<String, dynamic>;
        await localStorage.saveUserData(jsonEncode(_profile));
        _isLoading = false;
        notifyListeners();
        return true;
      }
      _error = 'Failed to update profile';
      _isLoading = false;
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }
}

