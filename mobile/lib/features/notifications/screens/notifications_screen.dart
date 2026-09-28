import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../notifications/providers/notifications_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';

class NotificationsScreen extends StatelessWidget {
  const NotificationsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Notifications'),
        actions: [
          Consumer<NotificationsProvider>(
            builder: (context, provider, _) {
              return TextButton.icon(
                onPressed: provider.markAllAsRead,
                icon: const Icon(Icons.done_all_rounded),
                label: const Text('Mark all read'),
              );
            },
          ),
        ],
      ),
      drawer: const AppDrawer(),
      body: Consumer<NotificationsProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.notifications.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.notifications.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: () => provider.fetchNotifications(),
            );
          }
          return RefreshIndicator(
            onRefresh: () async {
              await provider.fetchNotifications();
              await provider.fetchUnreadCount();
            },
            child: provider.notifications.isEmpty
                ? const Center(
                    child: Text('No notifications yet'),
                  )
                : ListView.builder(
                    padding: const EdgeInsets.all(16),
                    itemCount: provider.notifications.length,
                    itemBuilder: (context, index) {
                      final notification = provider.notifications[index];
                      return Card(
                        margin: const EdgeInsets.only(bottom: 12),
                        color: notification.isRead
                            ? null
                            : Theme.of(context).colorScheme.primaryContainer,
                        child: ListTile(
                          title: Text(
                            notification.title,
                            style: TextStyle(
                              fontWeight: notification.isRead ? FontWeight.normal : FontWeight.w600,
                            ),
                          ),
                          subtitle: Text(notification.message),
                          trailing: notification.isRead
                              ? null
                              : IconButton(
                                  icon: const Icon(Icons.visibility_rounded),
                                  onPressed: () => provider.markAsRead(notification.id),
                                ),
                          onTap: notification.isRead
                              ? null
                              : () => provider.markAsRead(notification.id),
                        ),
                      );
                    },
                  ),
          );
        },
      ),
      bottomNavigationBar: const AppBottomNav(currentIndex: -1),
    );
  }
}
