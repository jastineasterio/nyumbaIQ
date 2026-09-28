import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../extensions/providers/extensions_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/theme/app_text_styles.dart';

class ExtensionFormScreen extends StatefulWidget {
  const ExtensionFormScreen({super.key});

  @override
  State<ExtensionFormScreen> createState() => _ExtensionFormScreenState();
}

class _ExtensionFormScreenState extends State<ExtensionFormScreen> {
  final _formKey = GlobalKey<FormState>();
  final _endDateController = TextEditingController();
  final _notesController = TextEditingController();
  bool _isSubmitting = false;

  @override
  void dispose() {
    _endDateController.dispose();
    _notesController.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;

    setState(() => _isSubmitting = true);

    final provider = context.read<ExtensionsProvider>();
    final success = await provider.createExtension({
      'proposedEndDate': _endDateController.text.trim(),
      'notes': _notesController.text.trim().isEmpty ? null : _notesController.text.trim(),
    });

    setState(() => _isSubmitting = false);

    if (success && mounted) {
      Navigator.pop(context);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Extension request submitted')),
      );
    } else if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(provider.error ?? 'Failed to submit extension')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Request Extension'),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            TextFormField(
              controller: _endDateController,
              decoration: const InputDecoration(
                labelText: 'Proposed End Date (YYYY-MM-DD)',
                prefixIcon: Icon(Icons.calendar_today_rounded),
              ),
              validator: (v) => v?.trim().isEmpty == true ? 'Enter proposed end date' : null,
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _notesController,
              decoration: const InputDecoration(
                labelText: 'Notes (optional)',
                prefixIcon: Icon(Icons.notes_rounded),
              ),
              maxLines: 3,
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
