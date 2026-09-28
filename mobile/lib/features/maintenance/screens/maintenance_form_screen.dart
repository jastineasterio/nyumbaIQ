import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../maintenance/providers/maintenance_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/theme/app_text_styles.dart';

class MaintenanceFormScreen extends StatefulWidget {
  final String? requestId;

  const MaintenanceFormScreen({super.key, this.requestId});

  @override
  State<MaintenanceFormScreen> createState() => _MaintenanceFormScreenState();
}

class _MaintenanceFormScreenState extends State<MaintenanceFormScreen> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _priorityController = TextEditingController(text: 'MEDIUM');
  bool _isSubmitting = false;

  @override
  void dispose() {
    _titleController.dispose();
    _descriptionController.dispose();
    _priorityController.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isSubmitting = true);

    final provider = context.read<MaintenanceProvider>();
    final success = await provider.createRequest({
      'title': _titleController.text.trim(),
      'description': _descriptionController.text.trim(),
      'priority': _priorityController.text.trim(),
      'status': 'OPEN',
    });

    setState(() => _isSubmitting = false);

    if (success && mounted) {
      Navigator.pop(context);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Maintenance request created')),
      );
    } else if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(provider.error ?? 'Failed to create request')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'New Request'),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            TextFormField(
              controller: _titleController,
              decoration: const InputDecoration(
                labelText: 'Title',
                prefixIcon: Icon(Icons.title_rounded),
              ),
              validator: (v) => v?.trim().isEmpty == true ? 'Enter title' : null,
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _descriptionController,
              decoration: const InputDecoration(
                labelText: 'Description',
                prefixIcon: Icon(Icons.description_rounded),
              ),
              maxLines: 4,
              validator: (v) => v?.trim().isEmpty == true ? 'Enter description' : null,
            ),
            const SizedBox(height: 16),
            DropdownButtonFormField<String>(
              value: _priorityController.text,
              decoration: const InputDecoration(
                labelText: 'Priority',
                prefixIcon: Icon(Icons.warning_rounded),
              ),
              items: const [
                DropdownMenuItem(value: 'LOW', child: Text('Low')),
                DropdownMenuItem(value: 'MEDIUM', child: Text('Medium')),
                DropdownMenuItem(value: 'HIGH', child: Text('High')),
                DropdownMenuItem(value: 'URGENT', child: Text('Urgent')),
              ],
              onChanged: (v) {
                if (v != null) _priorityController.text = v;
              },
            ),
            const SizedBox(height: 24),
            ElevatedButton(
              onPressed: _isSubmitting ? null : _submit,
              child: _isSubmitting
                  ? const SizedBox(
                      width: 20,
                      height: 20,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : const Text('Submit Request'),
            ),
          ],
        ),
      ),
    );
  }
}
