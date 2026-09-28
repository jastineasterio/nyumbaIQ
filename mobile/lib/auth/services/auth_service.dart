import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import '../../core/errors/exceptions.dart';
import '../models/login_request.dart';
import '../models/login_response.dart';
import '../../core/network/api_client.dart';
import '../../core/network/api_endpoints.dart';
import '../../core/storage/secure_storage.dart';
import '../../core/storage/local_storage.dart';
import '../../core/constants/app_constants.dart';

class AuthService {
  final ApiClient apiClient;
  final SecureStorageService secureStorage;
  final LocalStorageService localStorage;

  AuthService(this.apiClient, this.secureStorage, this.localStorage);

  Future<LoginResponse> login(LoginRequest request) async {
    final response = await apiClient.post(
      ApiEndpoints.authLogin,
      body: request.toJson(),
      includeAuth: false,
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      final json = jsonDecode(response.body) as Map<String, dynamic>;
      final loginResponse = LoginResponse.fromJson(json);

      await secureStorage.saveAccessToken(loginResponse.accessToken);
      await secureStorage.saveRefreshToken(loginResponse.refreshToken);
      await localStorage.saveUserData(jsonEncode(loginResponse.user.toJson()));

      return loginResponse;
    } else if (response.statusCode == 401) {
      throw AuthException('Invalid email or password');
    } else {
      final error = _parseError(response);
      throw ServerException(error.message, error.status);
    }
  }

  Future<TokenPair> refreshToken() async {
    final refreshToken = await secureStorage.getRefreshToken();
    if (refreshToken == null) {
      throw AuthException('No refresh token available');
    }

    final response = await apiClient.post(
      ApiEndpoints.authRefresh,
      body: {'refreshToken': refreshToken},
      includeAuth: false,
    );

    if (response.statusCode == 200 || response.statusCode == 201) {
      final json = jsonDecode(response.body) as Map<String, dynamic>;
      final tokenPair = TokenPair.fromJson(json);
      await secureStorage.saveAccessToken(tokenPair.accessToken);
      await secureStorage.saveRefreshToken(tokenPair.refreshToken);
      return tokenPair;
    } else {
      await secureStorage.clearTokens();
      throw AuthException('Session expired. Please login again.');
    }
  }

  Future<void> logout() async {
    try {
      final token = await secureStorage.getAccessToken();
      if (token != null) {
        await apiClient.post(
          ApiEndpoints.authLogout,
          includeAuth: true,
        );
      }
    } catch (e) {
      if (kDebugMode) print('Logout error: $e');
    } finally {
      await secureStorage.clearTokens();
      await localStorage.clearUserData();
    }
  }

  Future<void> changePassword(String currentPassword, String newPassword) async {
    final response = await apiClient.post(
      ApiEndpoints.authChangePassword,
      body: {
        'currentPassword': currentPassword,
        'newPassword': newPassword,
      },
    );

    if (response.statusCode != 204) {
      final error = _parseError(response);
      throw ServerException(error.message, error.status);
    }
  }

  ApiError _parseError(http.Response response) {
    try {
      final json = jsonDecode(response.body) as Map<String, dynamic>;
      return ApiError.fromJson(json);
    } catch (e) {
      return ApiError(
        status: response.statusCode,
        error: 'Error',
        message: response.body.isNotEmpty ? response.body : 'An error occurred',
        path: '',
      );
    }
  }
}
