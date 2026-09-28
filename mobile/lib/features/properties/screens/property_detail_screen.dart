import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../core/widgets/empty_state_widget.dart';
import '../../../core/theme/app_text_styles.dart';

class PropertyDetailScreen extends StatelessWidget {
  final String propertyId;

  const PropertyDetailScreen({super.key, required this.propertyId});

  @override
  Widget build(BuildContext context) {
    final property = {'id': propertyId, 'name': 'Property Details'};

    return Scaffold(
      appBar: const CustomAppBar(title: 'Property Details', showBackButton: true),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24),
          child: Column(
            children: [
              Icon(
                Icons.apartment_rounded,
                size: 80,
                color: Theme.of(context).colorScheme.primary,
              ),
              const SizedBox(height: 24),
              Text(
                property['name'] ?? 'Property',
                style: Theme.of(context).textTheme.headlineMedium,
              ),
              const SizedBox(height: 8),
              Text(
                'ID: $propertyId',
                style: AppTextStyles.bodySmall,
              ),
              const SizedBox(height: 24),
              ElevatedButton.icon(
                onPressed: () {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('View buildings feature coming soon')),
                  );
                },
                icon: const Icon(Icons.location_city_rounded),
                label: const Text('View Buildings'),
              ),
              const SizedBox(height: 12),
              OutlinedButton.icon(
                onPressed: () {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Edit property feature coming soon')),
                  );
                },
                icon: const Icon(Icons.edit_rounded),
                label: const Text('Edit Property'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
