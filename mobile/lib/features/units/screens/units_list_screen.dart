import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/units_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/app_drawer.dart';
import '../../../core/widgets/app_bottom_nav.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/widgets/error_widget.dart';
import '../../../core/widgets/empty_state_widget.dart';
import '../../../config/routes.dart';
import '../../../core/theme/app_text_styles.dart';

class UnitsListScreen extends StatefulWidget {
  const UnitsListScreen({super.key});

  @override
  State<UnitsListScreen> createState() => _UnitsListScreenState();
}

class _UnitsListScreenState extends State<UnitsListScreen> {
  final ScrollController _scrollController = ScrollController();
  String? _selectedPropertyId;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _loadUnits();
    });
    _scrollController.addListener(_onScroll);
  }

  void _onScroll() {
    if (_scrollController.position.pixels >=
        _scrollController.position.maxScrollExtent - 200) {
      final provider = context.read<UnitsProvider>();
      if (provider.hasMore && !provider.isLoading) {
        provider.fetchUnits(propertyId: _selectedPropertyId, page: provider.units.length ~/ 20 + 1);
      }
    }
  }

  Future<void> _loadUnits() async {
    final provider = context.read<UnitsProvider>();
    await provider.fetchUnits(propertyId: _selectedPropertyId);
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Units'),
      drawer: const AppDrawer(),
      body: Consumer<UnitsProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading && provider.units.isEmpty) {
            return const LoadingIndicator(size: 40);
          }
          if (provider.error != null && provider.units.isEmpty) {
            return CustomErrorWidget(
              message: provider.error!,
              onRetry: _loadUnits,
            );
          }
          if (provider.units.isEmpty) {
            return EmptyStateWidget(
              icon: Icons.door_front_door_rounded,
              title: 'No Units',
              message: 'Add units to your floors.',
              actionLabel: 'Add Unit',
              onAction: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Create unit form coming soon')),
                );
              },
            );
          }
          return RefreshIndicator(
            onRefresh: _loadUnits,
            child: ListView.builder(
              controller: _scrollController,
              padding: const EdgeInsets.all(16),
              itemCount: provider.units.length + (provider.hasMore ? 1 : 0),
              itemBuilder: (context, index) {
                if (index >= provider.units.length) {
                  return const Padding(
                    padding: EdgeInsets.all(16),
                    child: LoadingIndicator(),
                  );
                }
                final unit = provider.units[index];
                final status = unit['status'] as String? ?? 'VACANT';
                return Card(
                  margin: const EdgeInsets.only(bottom: 12),
                  child: ListTile(
                    contentPadding: const EdgeInsets.all(16),
                    leading: CircleAvatar(
                      backgroundColor: Theme.of(context).colorScheme.primaryContainer,
                      child: Icon(
                        Icons.door_front_door_rounded,
                        color: Theme.of(context).colorScheme.primary,
                      ),
                    ),
                    title: Text(
                      unit['unitNumber'] as String? ?? 'Unnamed Unit',
                      style: Theme.of(context).textTheme.titleMedium,
                    ),
                    subtitle: Text(
                      '${unit['propertyName'] as String? ?? ''} ${unit['floorName'] as String? ?? ''}',
                    ),
                    trailing: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                      decoration: BoxDecoration(
                        color: status == 'OCCUPIED'
                            ? Theme.of(context).colorScheme.secondaryContainer
                            : Theme.of(context).colorScheme.errorContainer,
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: Text(
                        status,
                        style: AppTextStyles.labelSmall,
                      ),
                    ),
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
                Navigator.pushNamed(context, AppRoutes.unitForm);
              },
              child: const Icon(Icons.add_rounded),
            );
          }
          return const SizedBox.shrink();
        },
      ),
      bottomNavigationBar: const AppBottomNav(currentIndex: 2),
    );
  }
}
