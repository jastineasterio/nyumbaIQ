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
import '../../../core/theme/app_text_styles.dart';

class KycDocumentsScreen extends StatelessWidget {
  const KycDocumentsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'KYC Documents'),
      drawer: const AppDrawer(),
      body: Consumer<KycProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.documents.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.documents.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: provider.fetchKycDocuments,
            );
          }
          if (provider.documents.isEmpty) {
            return EmptyStateWidget(
              icon: Icons.folder_rounded,
              title: 'No Documents',
              message: 'Upload KYC documents to get verified.',
              actionLabel: 'Upload Document',
              onAction: () {
                Navigator.pushNamed(context, AppRoutes.kycUpload);
              },
            );
          }
          return RefreshIndicator(
            onRefresh: provider.fetchKycDocuments,
            child: ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: provider.documents.length,
              itemBuilder: (context, index) {
                final doc = provider.documents[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    contentPadding: const EdgeInsets.all(16),
                    leading: CircleAvatar(
                      backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                      child: Icon(
                        Icons.description_rounded,
                        color: Theme.of(context).colorScheme.primary,
                      ),
                    ),
                    title: Text(
                      doc['documentType'] as String? ?? 'Document',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    subtitle: Text(
                      doc['status'] as String? ?? 'PENDING',
                    ),
                    trailing: const Icon(Icons.chevron_right_rounded),
                  ),
                );
              },
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
