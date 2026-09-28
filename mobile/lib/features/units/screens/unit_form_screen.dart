import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/units_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/theme/app_text_styles.dart';

class UnitFormScreen extends StatefulWidget {
  final String? floorId;
  final String? unitId;

  const UnitFormScreen({
    super.key,
    this.floorId,
    this.unitId,
  });

  @override
  State<UnitFormScreen> createState() => _UnitFormScreenState();
}

class _UnitFormScreenState extends State<UnitFormScreen> {
  final _formKey = GlobalKey<FormState>();
  final _unitNumberController = TextEditingController();
  final _rentController = TextEditingController();
  String _status = 'VACANT';
  bool _isLoading = false;

  @override
  void dispose() {
    _unitNumberController.dispose();
    _rentController.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    if (!_formKey.currentState!.validate()) return;
    setState(() => _isLoading = true);

    final provider = context.read<UnitsProvider>();
    final data = {
      'unitNumber': _unitNumberController.text.trim(),
      'monthlyRent': double.tryParse(_rentController.text.trim()) ?? 0,
      'status': _status,
    };

    bool success;
    if (widget.unitId != null) {
      success = await provider.updateUnit(widget.unitId!, data);
    } else {
      success = await provider.createUnit(data);
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
    final isEdit = widget.unitId != null;
    return Scaffold(
      appBar: CustomAppBar(title: isEdit ? 'Edit Unit' : 'Add Unit'),
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
                      controller: _unitNumberController,
                      decoration: const InputDecoration(
                        labelText: 'Unit Number',
                        hintText: 'e.g. A1',
                      ),
                      validator: (v) {
                        if (v == null || v.trim().isEmpty) return 'Required';
                        return null;
                      },
                    ),
                    const SizedBox(height: 16),
                    TextFormField(
                      controller: _rentController,
                      keyboardType: const TextInputType.numberWithOptions(decimal: true),
                      decoration: const InputDecoration(
                        labelText: 'Monthly Rent',
                        hintText: 'e.g. 15000',
                      ),
                      validator: (v) {
                        if (v == null || v.trim().isEmpty) return 'Required';
                        final n = double.tryParse(v.trim());
                        if (n == null || n <= 0) return 'Enter a positive amount';
                        return null;
                      },
                    ),
                    const SizedBox(height: 16),
                    DropdownButtonFormField<String>(
                      value: _status,
                      decoration: const InputDecoration(
                        labelText: 'Status',
                      ),
                      items: const [
                        DropdownMenuItem(value: 'VACANT', child: Text('Vacant')),
                        DropdownMenuItem(value: 'OCCUPIED', child: Text('Occupied')),
                        DropdownMenuItem(value: 'MAINTENANCE', child: Text('Maintenance')),
                      ],
                      onChanged: (v) => setState(() => _status = v ?? 'VACANT'),
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
