import 'package:flutter/material.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/theme/app_text_styles.dart';

class TenantDetailScreen extends StatelessWidget {
  final String tenantId;

  const TenantDetailScreen({super.key, required this.tenantId});

  @override
  Widget build(BuildContext context) {
    final tenant = {
      'id': tenantId,
      'firstName': 'John',
      'lastName': 'Doe',
      'email': 'john@example.com',
      'phone': '+254 712 345 678',
    };

    return Scaffold(
      appBar: const CustomAppBar(title: 'Tenant Details', showBackButton: true),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Center(
            child: CircleAvatar(
              radius: 48,
              backgroundColor: Theme.of(context).colorScheme.secondaryContainer,
              child: Icon(
                Icons.person_rounded,
                size: 64,
                color: Theme.of(context).colorScheme.secondary,
              ),
            ),
          ),
          const SizedBox(height: 24),
          Text(
            '${tenant['firstName']} ${tenant['lastName']}',
            textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 24),
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  _DetailRow(icon: Icons.email_rounded, label: 'Email', value: tenant['email'] as String? ?? 'N/A'),
                  const SizedBox(height: 12),
                  _DetailRow(icon: Icons.phone_rounded, label: 'Phone', value: tenant['phone'] as String? ?? 'N/A'),
                  const SizedBox(height: 12),
                  _DetailRow(icon: Icons.badge_rounded, label: 'ID', value: tenant['id'] as String? ?? 'N/A'),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _DetailRow extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;

  const _DetailRow({
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
