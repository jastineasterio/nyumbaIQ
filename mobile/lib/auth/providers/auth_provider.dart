import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../core/errors/exceptions.dart';
import '../../core/storage/secure_storage.dart';
import '../../core/storage/local_storage.dart';
import '../models/user_model.dart';
import '../models/login_request.dart';
import '../services/auth_service.dart';
import '../../core/network/api_endpoints.dart';

class AuthProvider extends ChangeNotifier {
  final AuthService authService;
  final SecureStorageService secureStorage;
  final LocalStorageService localStorage;

  UserModel? _user;
  bool _isLoading = false;
  String? _error;

  AuthProvider({
    required this.authService,
    required this.secureStorage,
    required this.localStorage,
  });

  UserModel? get user => _user;
  bool get isAuthenticated => _user != null;
  bool get isLoading => _isLoading;
  String? get error => _error;
  String? get role => _user?.role;

  Future<void> initialize(AuthService service) async {
    _isLoading = true;
    notifyListeners();

    try {
      final token = await secureStorage.getAccessToken();
      if (token != null) {
        final userData = localStorage.getUserData();
        if (userData != null) {
          final json = jsonDecode(userData) as Map<String, dynamic>;
          _user = UserModel.fromJson(json);
          notifyListeners();

          try {
            await service.refreshToken();
            await fetchCurrentUser();
          } on AuthException catch (_) {
            await logout();
          } catch (e) {
            if (kDebugMode) print('Auth init error: $e');
          }
        }
      }
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<bool> login(String email, String password) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await authService.login(
        LoginRequest(email: email, password: password),
      );
      _user = response.user;
      await localStorage.saveUserData(jsonEncode(response.user.toJson()));
      _isLoading = false;
      notifyListeners();
      return true;
    } on AuthException catch (e) {
      _error = e.message;
      _isLoading = false;
      notifyListeners();
      return false;
    } on ServerException catch (e) {
      _error = e.message;
      _isLoading = false;
      notifyListeners();
      return false;
    } catch (e) {
      _error = 'An unexpected error occurred';
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  Future<void> fetchCurrentUser() async {
    try {
      final apiClient = authService.apiClient;
      final response = await apiClient.get(ApiEndpoints.usersMe);
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        _user = UserModel.fromJson(json);
        await localStorage.saveUserData(jsonEncode(_user!.toJson()));
        notifyListeners();
      }
    } catch (e) {
      if (kDebugMode) print('Fetch user error: $e');
    }
  }

  Future<void> updateProfile(Map<String, dynamic> data) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final apiClient = authService.apiClient;
      final response = await apiClient.put(ApiEndpoints.usersMe, body: data);
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        _user = UserModel.fromJson(json);
        await localStorage.saveUserData(jsonEncode(_user!.toJson()));
        _isLoading = false;
        notifyListeners();
      } else {
        throw ServerException('Failed to update profile');
      }
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      rethrow;
    }
  }

  Future<void> logout() async {
    _isLoading = true;
    notifyListeners();
    await authService.logout();
    _user = null;
    _error = null;
    _isLoading = false;
    notifyListeners();
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }
}
