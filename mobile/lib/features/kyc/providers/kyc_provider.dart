import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';
import '../../../core/constants/api_constants.dart';

class KycProvider extends ChangeNotifier {
  final ApiClient apiClient;
  Map<String, dynamic>? _profile;
  List<dynamic> _documents = [];
  bool _isLoading = false;
  String? _error;

  KycProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  Map<String, dynamic>? get profile => _profile;
  List<dynamic> get documents => _documents;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> fetchKycProfile() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await apiClient.get(ApiEndpoints.kycProfile);
      if (response.statusCode == 200) {
        _profile = jsonDecode(response.body) as Map<String, dynamic>;
      } else {
        _error = 'Failed to load KYC profile';
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

  Future<void> fetchKycDocuments() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await apiClient.get(ApiEndpoints.kycDocuments);
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        _documents = json['documents'] as List<dynamic>? ?? [];
      } else {
        _error = 'Failed to load documents';
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

  Future<bool> uploadDocument(String documentType, String filePath) async {
    try {
      final request = http.MultipartRequest(
        'POST',
        Uri.parse('${ApiConstants.baseUrl}${ApiEndpoints.kycUpload}'),
      );
      request.fields['documentType'] = documentType;

      final authToken = await apiClient.secureStorage?.getAccessToken();
      if (authToken != null) {
        request.headers['Authorization'] = 'Bearer $authToken';
      }

      final streamedResponse = await request.send();
      final response = await http.Response.fromStream(streamedResponse);
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchKycDocuments();
        return true;
      }
      _error = 'Failed to upload document';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }

  Future<bool> submitKyc(Map<String, dynamic> data) async {
    try {
      final response = await apiClient.post(ApiEndpoints.kycProfile, body: data);
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchKycProfile();
        return true;
      }
      _error = 'Failed to submit KYC';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
