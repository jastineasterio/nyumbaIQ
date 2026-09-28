import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/properties_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../core/widgets/empty_state_widget.dart';
import '../../../config/routes.dart';
import 'property_detail_screen.dart';
import '../../../core/theme/app_text_styles.dart';

class PropertiesListScreen extends StatefulWidget {
  const PropertiesListScreen({super.key});

  @override
  State<PropertiesListScreen> createState() => _PropertiesListScreenState();
}

class _PropertiesListScreenState extends State<PropertiesListScreen> {
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<PropertiesProvider>().fetchProperties();
    });
    _scrollController.addListener(_onScroll);
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      final provider = context.read<PropertiesProvider>();
      if (provider.hasMore && !provider.isLoading) {
        provider.fetchProperties(page: provider.properties.length ~/ 20 + 1);
      }
    }
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Properties'),
      drawer: const AppDrawer(),
      body: Consumer<PropertiesProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.properties.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.properties.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: () => provider.fetchProperties(),
            );
          }
          if (provider.properties.isEmpty) {
            return EmptyStateWidget(
              icon: Icons.apartment_rounded,
              title: 'No Properties',
              message: 'Get started by adding your first property.',
              actionLabel: 'Add Property',
              onAction: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Create property form coming soon')),
                );
              },
            );
          }
          return RefreshIndicator(
            onRefresh: () => provider.fetchProperties(),
            child: ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.all(16),
              itemCount: provider.properties.length + (provider.hasMore ? 1 : 0),
              itemBuilder: (context, index) {
                if (index >= provider.properties.length) {
                  return const Padding(
                    padding: EdgeInsets.all(16),
                    child: LoadingIndicator(),
                  );
                }
                final property = provider.properties[index];
                return PropertyCard(
                  property: property,
                  onTap: () {
                    final id = property['id']?.toString() ?? '';
                    Navigator.pushNamed(
                      context,
                      AppRoutes.propertyDetail,
                      arguments: id,
                    );
                  },
                );
              },
            ),
          );
        },
      ),
      floatingActionButton: Consumer<AuthProvider>(
        builder: (context, auth, _) {
          if (auth.role == 'OWNER' || auth.role == 'MANAGER') {
            return FloatingActionButton(
              onPressed: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Create property form coming soon')),
                );
              },
              child: const Icon(Icons.add_rounded),
            );
          }
          return const SizedBox.shrink();
        },
      ),
      bottomNavigationBar: const AppBottomNav(currentIndex: 1),
    );
  }
}

class PropertyCard extends StatelessWidget {
  final Map<String, dynamic> property;
  final VoidCallback? onTap;

  const PropertyCard({
    super.key,
    required this.property,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final name = property['name'] as String? ?? 'Unnamed Property';
    final location = property['location'] as String? ?? '';
    final status = property['status'] as String? ?? 'ACTIVE';

    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: ListTile(
        contentPadding: const EdgeInsets.all(16),
        leading: CircleAvatar(
          backgroundColor: Theme.of(context).colorScheme.primaryContainer,
          child: Icon(
            Icons.apartment_rounded,
            color: Theme.of(context).colorScheme.primary,
          ),
        ),
        title: Text(
          name,
          style: Theme.of(context).textTheme.titleMedium,
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (location.isNotEmpty) Text(location),
            Text(
              status,
              style: AppTextStyles.caption.copyWith(
                color: status == 'ACTIVE'
                    ? Theme.of(context).colorScheme.secondary
                    : Theme.of(context).colorScheme.error,
              ),
            ),
          ],
        ),
        trailing: const Icon(Icons.chevron_right_rounded),
        onTap: onTap,
      ),
    );
  }
}
