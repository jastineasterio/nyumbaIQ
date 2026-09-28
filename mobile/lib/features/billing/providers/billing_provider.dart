import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';
import '../models/billing_schedule_model.dart';

class BillingProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<BillingScheduleModel> _schedules = [];
  bool _isLoading = false;
  String? _error;

  BillingProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<BillingScheduleModel> get schedules => _schedules;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> fetchSchedules() async {
    if (_isLoading) return;

    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await apiClient.getBillingSchedules();
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        _schedules = content.map((e) => BillingScheduleModel.fromJson(e as Map<String, dynamic>)).toList();
      } else {
        _error = 'Failed to load billing schedules';
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

  Future<bool> generateBilling(Map<String, dynamic> data) async {
    try {
      final response = await apiClient.generateBilling(data);
      if (response.statusCode == 200 || response.statusCode == 201) {
        await fetchSchedules();
        return true;
      }
      _error = 'Failed to generate billing';
      notifyListeners();
      return false;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
