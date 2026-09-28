import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/buildings_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../core/widgets/empty_state_widget.dart';
import '../../../config/routes.dart';
import '../../../core/theme/app_text_styles.dart';

class BuildingsListScreen extends StatefulWidget {
  const BuildingsListScreen({super.key});

  @override
  State<BuildingsListScreen> createState() => _BuildingsListScreenState();
}

class _BuildingsListScreenState extends State<BuildingsListScreen> {
  final ScrollController _scrollController = ScrollController();
  String? _selectedPropertyId;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadBuildings();
    });
    _scrollController.addListener(_onScroll);
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      final provider = context.read<BuildingsProvider>();
      if (provider.hasMore && !provider.isLoading) {
        provider.fetchBuildings(_selectedPropertyId ?? '', page: provider.buildings.length ~/ 20 + 1);
      }
    }
  }

  Future<void> _loadBuildings() async {
    final provider = context.read<BuildingsProvider>();
    await provider.fetchBuildings(_selectedPropertyId ?? '');
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Buildings'),
      drawer: const AppDrawer(),
      body: Consumer<BuildingsProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.buildings.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.buildings.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: _loadBuildings,
            );
          }
          if (provider.buildings.isEmpty) {
            return EmptyStateWidget(
              icon: Icons.location_city_rounded,
              title: 'No Buildings',
              message: 'Add buildings to your properties.',
              actionLabel: 'Add Building',
              onAction: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Create building form coming soon')),
                );
              },
            );
          }
          return RefreshIndicator(
            onRefresh: _loadBuildings,
            child: ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.all(16),
              itemCount: provider.buildings.length + (provider.hasMore ? 1 : 0),
              itemBuilder: (context, index) {
                if (index >= provider.buildings.length) {
                  return const Padding(
                    padding: EdgeInsets.all(16),
                    child: LoadingIndicator(),
                  );
                }
                final building = provider.buildings[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    contentPadding: const EdgeInsets.all(16),
                    leading: CircleAvatar(
                      backgroundColor: Theme.of(context).colorScheme.secondaryContainer,
                      child: Icon(
                        Icons.location_city_rounded,
                        color: Theme.of(context).colorScheme.secondary,
                      ),
                    ),
                    title: Text(
                      building['name'] as String? ?? 'Unnamed Building',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    subtitle: Text(
                      building['propertyName'] as String? ?? '',
                    ),
                    trailing: const Icon(Icons.chevron_right_rounded),
                    onTap: () {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('Building detail view coming soon')),
                      );
                    },
                  ),
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
                Navigator.pushNamed(context, AppRoutes.buildingForm);
              },
              child: const Icon(Icons.add_rounded),
            );
          }
          return const SizedBox.shrink();
        },
      ),
    );
  }
}
