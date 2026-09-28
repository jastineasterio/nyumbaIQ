import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/profile_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../config/routes.dart';
import '../../../core/theme/app_text_styles.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Profile'),
      drawer: const AppDrawer(),
      body: Consumer2<ProfileProvider, AuthProvider>(
        builder: (context, profileProvider, authProvider, child) {
          if (profileProvider.isLoading && profileProvider.profile == null) {
            return const LoadingIndicator(size: 40);
          }
          if (profileProvider.error != null && profileProvider.profile == null) {
            return CustomErrorWidget(
              message: profileProvider.error!,
              onRetry: profileProvider.fetchProfile,
            );
          }
          final profile = profileProvider.profile ?? {};
          final user = authProvider.user;

          return RefreshIndicator(
            onRefresh: profileProvider.fetchProfile,
            child: ListView(
              padding: const EdgeInsets.all(16),
              children: [
                Center(
                  child: CircleAvatar(
                    radius: 48,
                    backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                    child: Text(
                      user != null
                          ? '${user.firstName[0]}${user.lastName[0]}'
                          : 'U',
                      style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                            color: Theme.of(context).colorScheme.primary,
                          ),
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                Text(
                  user?.fullName ?? 'User',
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.headlineSmall,
                ),
                Text(
                  user?.role ?? '',
                  textAlign: TextAlign.center,
                  style: AppTextStyles.bodySmall,
                ),
                const SizedBox(height: 24),
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      children: [
                        _ProfileRow(
                          icon: Icons.email_rounded,
                          label: 'Email',
                          value: profile['email'] as String? ?? user?.email ?? 'N/A',
                        ),
                        const SizedBox(height: 12),
                        _ProfileRow(
                          icon: Icons.phone_rounded,
                          label: 'Phone',
                          value: profile['phone'] as String? ?? user?.phone ?? 'N/A',
                        ),
                        const SizedBox(height: 12),
                        _ProfileRow(
                          icon: Icons.person_rounded,
                          label: 'Username',
                          value: profile['username'] as String? ?? user?.username ?? 'N/A',
                        ),
                        const SizedBox(height: 12),
                        _ProfileRow(
                          icon: Icons.calendar_today_rounded,
                          label: 'Joined',
                          value: profile['createdAt'] != null
                              ? DateTime.tryParse(profile['createdAt']) != null
                                  ? '${DateTime.tryParse(profile['createdAt'])!.day}/${DateTime.tryParse(profile['createdAt'])!.month}/${DateTime.tryParse(profile['createdAt'])!.year}'
                                  : 'N/A'
                              : 'N/A',
                        ),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                ElevatedButton.icon(
                  onPressed: () {
                    Navigator.pushNamed(context, AppRoutes.changePassword);
                  },
                  icon: const Icon(Icons.lock_rounded),
                  label: const Text('Change Password'),
                ),
              ],
            ),
          );
        },
      ),
      bottomNavigationBar: const AppBottomNav(currentIndex: 3),
    );
  }
}

class _ProfileRow extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;

  const _ProfileRow({
    required this.icon,
    required this.label,
    required this.value,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Icon(icon, color: Theme.of(context).colorScheme.primary, size: 20),
        const SizedBox(width: 12),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(label, style: AppTextStyles.caption),
              Text(value, style: Theme.of(context).textTheme.bodyMedium),
            ],
          ),
        ),
      ],
    );
  }
}
