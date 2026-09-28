import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/floors_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../core/widgets/empty_state_widget.dart';
import '../../../config/routes.dart';
import '../../../core/theme/app_text_styles.dart';

class FloorsListScreen extends StatefulWidget {
  const FloorsListScreen({super.key});

  @override
  State<FloorsListScreen> createState() => _FloorsListScreenState();
}

class _FloorsListScreenState extends State<FloorsListScreen> {
  final ScrollController _scrollController = ScrollController();
  String? _selectedBuildingId;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadFloors();
    });
    _scrollController.addListener(_onScroll);
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      final provider = context.read<FloorsProvider>();
      if (provider.hasMore && !provider.isLoading) {
        provider.fetchFloors(_selectedBuildingId ?? '', page: provider.floors.length ~/ 20 + 1);
      }
    }
  }

  Future<void> _loadFloors() async {
    final provider = context.read<FloorsProvider>();
    await provider.fetchFloors(_selectedBuildingId ?? '');
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Floors'),
      drawer: const AppDrawer(),
      body: Consumer<FloorsProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.floors.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.floors.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: _loadFloors,
            );
          }
          if (provider.floors.isEmpty) {
            return EmptyStateWidget(
              icon: Icons.layers_rounded,
              title: 'No Floors',
              message: 'Add floors to your buildings.',
              actionLabel: 'Add Floor',
              onAction: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Create floor form coming soon')),
                );
              },
            );
          }
          return RefreshIndicator(
            onRefresh: _loadFloors,
            child: ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.all(16),
              itemCount: provider.floors.length + (provider.hasMore ? 1 : 0),
              itemBuilder: (context, index) {
                if (index >= provider.floors.length) {
                  return const Padding(
                    padding: EdgeInsets.all(16),
                    child: LoadingIndicator(),
                  );
                }
                final floor = provider.floors[index];
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    contentPadding: const EdgeInsets.all(16),
                    leading: CircleAvatar(
                      backgroundColor: Theme.of(context).colorScheme.tertiaryContainer,
                      child: Icon(
                        Icons.layers_rounded,
                        color: Theme.of(context).colorScheme.tertiary,
                      ),
                    ),
                    title: Text(
                      floor['name'] as String? ?? 'Unnamed Floor',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    subtitle: Text(
                      floor['buildingName'] as String? ?? '',
                    ),
                    trailing: const Icon(Icons.chevron_right_rounded),
                    onTap: () {
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('Floor detail view coming soon')),
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
                Navigator.pushNamed(context, AppRoutes.floorForm);
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
