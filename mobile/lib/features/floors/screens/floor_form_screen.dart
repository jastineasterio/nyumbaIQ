import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/floors_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/theme/app_text_styles.dart';

class FloorFormScreen extends StatefulWidget {
  final String? buildingId;
  final String? floorId;

  const FloorFormScreen({
    super.key,
    this.buildingId,
    this.floorId,
  });

  @override
  State<FloorFormScreen> createState() => _FloorFormScreenState();
}

class _FloorFormScreenState extends State<FloorFormScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _descriptionController = TextEditingController();
  bool _isLoading = false;

  @override
  void dispose() {
    _nameController.dispose();
    _descriptionController.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _isLoading = true);

    final provider = context.read<FloorsProvider>();
    final data = {
      'name': _nameController.text.trim(),
      'description': _descriptionController.text.trim(),
    };

    bool success;
    if (widget.floorId != null) {
      success = await provider.updateFloor(widget.floorId!, data);
    } else {
      success = await provider.createFloor(widget.buildingId ?? '', data);
    }

    setState(() => _isLoading = false);
    if (success && mounted) {
      Navigator.pop(context);
    } else if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(provider.error ?? 'Operation failed'),
          backgroundColor: Theme.of(context).colorScheme.error,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final isEdit = widget.floorId != null;
    return Scaffold(
      appBar: CustomAppBar(title: isEdit ? 'Edit Floor' : 'Add Floor'),
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: ConstrainedBox(
              constraints: const BoxConstraints(maxWidth: 400),
              child: Form(
                key: _formKey,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    TextFormField(
                      controller: _nameController,
                      decoration: const InputDecoration(
                        labelText: 'Floor Name',
                        hintText: 'e.g. Ground Floor',
                      ),
                      validator: (v) {
                        if (v == null || v.trim().isEmpty) return 'Required';
                        return null;
                      },
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      controller: _descriptionController,
                      decoration: const InputDecoration(
                        labelText: 'Description',
                        hintText: 'Optional description',
                      ),
                      maxLines: 3,
                    ),
                    const SizedBox(height: 24),
                    SizedBox(
                      height: 48,
                      child: ElevatedButton(
                        onPressed: _isLoading ? null : _submit,
                        child: _isLoading
                            ? const LoadingIndicator(color: Colors.white)
                            : Text(isEdit ? 'Update' : 'Create'),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}
