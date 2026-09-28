import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/kyc_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../core/widgets/empty_state_widget.dart';
import '../../../config/routes.dart';
import '../../../core/theme/app_text_styles.dart';

class KycProfileScreen extends StatelessWidget {
  const KycProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'KYC Profile'),
      drawer: const AppDrawer(),
      body: Consumer<KycProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.profile == null) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.profile == null) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: provider.fetchKycProfile,
            );
          }
          final profile = provider.profile ?? {};
          return RefreshIndicator(
            onRefresh: provider.fetchKycProfile,
            child: ListView(
              padding: const EdgeInsets.all(16),
              children: [
                Center(
                  child: CircleAvatar(
                    radius: 48,
                    backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                    child: Icon(
                      Icons.verified_user_rounded,
                      size: 64,
                      color: Theme.of(context).colorScheme.primary,
                    ),
                  ),
                ),
                const SizedBox(height: 24),
                Text(
                  profile['fullName'] as String? ?? 'KYC Profile',
                  textAlign: TextAlign.center,
                  style: Theme.of(context).textTheme.headlineSmall,
                ),
                const SizedBox(height: 16),
                Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      children: [
                        _KycRow(label: 'Status', value: profile['status'] as String? ?? 'PENDING'),
                        const SizedBox(height: 12),
                        _KycRow(label: 'ID Number', value: profile['idNumber'] as String? ?? 'N/A'),
                        const SizedBox(height: 12),
                        _KycRow(label: 'Submitted', value: profile['submittedAt'] as String? ?? 'N/A'),
                      ],
                    ),
                  ),
                ),
                const SizedBox(height: 16),
                ElevatedButton.icon(
                  onPressed: () {
                    Navigator.pushNamed(context, AppRoutes.kycDocuments);
                  },
                  icon: const Icon(Icons.folder_rounded),
                  label: const Text('View Documents'),
                ),
              ],
            ),
          );
        },
      ),
      floatingActionButton: Consumer<AuthProvider>(
        builder: (context, auth, _) {
          if (auth.role == 'OWNER' || auth.role == 'MANAGER' || auth.role == 'TENANT') {
            return FloatingActionButton(
              onPressed: () {
                Navigator.pushNamed(context, AppRoutes.kycUpload);
              },
              child: const Icon(Icons.upload_rounded),
            );
          }
          return const SizedBox.shrink();
        },
      ),
    );
  }
}

class _KycRow extends StatelessWidget {
  final String label;
  final String value;

  const _KycRow({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(label, style: AppTextStyles.bodyMedium),
        Text(value, style: Theme.of(context).textTheme.bodyMedium),
      ],
    );
  }
}
