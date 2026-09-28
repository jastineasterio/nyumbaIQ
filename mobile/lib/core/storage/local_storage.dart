import 'package:shared_preferences/shared_preferences.dart';
import '../constants/api_constants.dart';

class LocalStorageService {
  static const String _tokenKey = AppConstants.tokenKey;
  static const String _refreshTokenKey = AppConstants.refreshTokenKey;
  static const String _userKey = AppConstants.userKey;
  static const String _onboardingKey = AppConstants.onboardingCompleted;

  final SharedPreferences prefs;

  LocalStorageService(this.prefs);

  Future<void> saveTokens(String accessToken, String refreshToken) async {
    await prefs.setString(_tokenKey, accessToken);
    await prefs.setString(_refreshTokenKey, refreshToken);
  }

  String? getAccessToken() => prefs.getString(_tokenKey);

  String? getRefreshToken() => prefs.getString(_refreshTokenKey);

  Future<void> clearTokens() async {
    await prefs.remove(_tokenKey);
    await prefs.remove(_refreshTokenKey);
  }

  Future<void> saveUserData(String userData) async {
    await prefs.setString(_userKey, userData);
  }

  String? getUserData() => prefs.getString(_userKey);

  Future<void> clearUserData() async {
    await prefs.remove(_userKey);
  }

  Future<void> setOnboardingCompleted(bool completed) async {
    await prefs.setBool(_onboardingKey, completed);
  }

  bool getOnboardingCompleted() => prefs.getBool(_onboardingKey) ?? false;

  Future<void> clearAll() async {
    await clearTokens();
    await clearUserData();
    await prefs.remove(_onboardingKey);
  }
}
