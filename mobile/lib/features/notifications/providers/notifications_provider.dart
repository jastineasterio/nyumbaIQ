import 'dart:convert';
import 'package:flutter/foundation.dart';
import '../../../core/errors/exceptions.dart';
import '../../../core/network/api_client.dart';
import '../../../core/network/api_endpoints.dart';
import '../models/notification_model.dart';

class NotificationsProvider extends ChangeNotifier {
  final ApiClient apiClient;
  List<NotificationModel> _notifications = [];
  int _unreadCount = 0;
  bool _isLoading = false;
  String? _error;

  NotificationsProvider() : apiClient = ApiClient(null);

  void initialize(ApiClient client) {
    apiClient.updateClient(client.client);
  }

  List<NotificationModel> get notifications => _notifications;
  int get unreadCount => _unreadCount;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> fetchNotifications({int page = 0, int size = 20}) async {
    if (_isLoading) return;

    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final queryParams = <String, dynamic>{'page': page, 'size': size};
      final response = await apiClient.getNotifications(queryParameters: queryParams);

      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        final content = json['content'] as List<dynamic>? ?? [];
        _notifications = content.map((e) => NotificationModel.fromJson(e as Map<String, dynamic>)).toList();
      } else {
        _error = 'Failed to load notifications';
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

  Future<void> fetchUnreadCount() async {
    try {
      final response = await apiClient.getUnreadNotificationsCount();
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body) as Map<String, dynamic>;
        _unreadCount = json['count'] as int? ?? 0;
        notifyListeners();
      }
    } catch (e) {
      if (kDebugMode) print('Get unread count error: $e');
    }
  }

  Future<bool> markAsRead(String id) async {
    try {
      final response = await apiClient.markNotificationAsRead(id);
      if (response.statusCode == 200) {
        final idx = _notifications.indexWhere((n) => n.id == id);
        if (idx != -1) {
          _notifications[idx] = NotificationModel(
            id: _notifications[idx].id,
            userId: _notifications[idx].userId,
            title: _notifications[idx].title,
            message: _notifications[idx].message,
            type: _notifications[idx].type,
            isRead: true,
            createdAt: _notifications[idx].createdAt,
          );
          if (_unreadCount > 0) _unreadCount--;
          notifyListeners();
        }
        return true;
      }
      return false;
    } catch (e) {
      if (kDebugMode) print('Mark as read error: $e');
      return false;
    }
  }

  Future<bool> markAllAsRead() async {
    try {
      final response = await apiClient.markAllNotificationsAsRead();
      if (response.statusCode == 200) {
        _unreadCount = 0;
        _notifications = _notifications.map((n) => NotificationModel(
          id: n.id,
          userId: n.userId,
          title: n.title,
          message: n.message,
          type: n.type,
          isRead: true,
          createdAt: n.createdAt,
        )).toList();
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      if (kDebugMode) print('Mark all as read error: $e');
      return false;
    }
  }
}
