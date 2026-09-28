import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/kyc_provider.dart';
import '../../../auth/providers/auth_provider.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';
import '../../../core/theme/app_text_styles.dart';

class KycUploadScreen extends StatefulWidget {
  final String documentType;

  const KycUploadScreen({
    super.key,
    required this.documentType,
  });

  @override
  State<KycUploadScreen> createState() => _KycUploadScreenState();
}

class _KycUploadScreenState extends State<KycUploadScreen> {
  final _formKey = GlobalKey<FormState>();
  String? _selectedFile;
  bool _isLoading = false;

  Future<void> _pickFile() async {
    // In a real app, use file_picker here
    setState(() => _selectedFile = 'sample_document.pdf');
  }

  Future<void> _upload() async {
    if (!_formKey.currentState!.validate()) return;
    if (_selectedFile == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Please select a file')),
      );
      return;
    }

    setState(() => _isLoading = true);
    final success = await context.read<KycProvider>().uploadDocument(
          widget.documentType,
          _selectedFile!,
        );
    setState(() => _isLoading = false);

    if (success && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Document uploaded successfully')),
      );
      Navigator.pop(context);
    } else if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(context.read<KycProvider>().error ?? 'Upload failed'),
          backgroundColor: Theme.of(context).colorScheme.error,
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: CustomAppBar(title: 'Upload ${widget.documentType.toUpperCase()}'),
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
                    Container(
                      height: 160,
                      decoration: BoxDecoration(
                        border: Border.all(color: Theme.of(context).dividerColor),
                        borderRadius: BorderRadius.circular(12),
                      ),
                      child: Center(
                        child: _selectedFile != null
                            ? Column(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Icon(
                                    Icons.description_rounded,
                                    size: 48,
                                    color: Theme.of(context).colorScheme.primary,
                                  ),
                                  const SizedBox(height: 8),
                                  Text(
                                    _selectedFile!,
                                    style: AppTextStyles.bodySmall,
                                  ),
                                ],
                              )
                            : Column(
                                mainAxisAlignment: MainAxisAlignment.center,
                                children: [
                                  Icon(
                                    Icons.cloud_upload_rounded,
                                    size: 48,
                                    color: Theme.of(context).disabledColor,
                                  ),
                                  const SizedBox(height: 8),
                                  Text(
                                    'Tap to select a file',
                                    style: AppTextStyles.bodySmall,
                                  ),
                                ],
                              ),
                      ),
                    ),
                    const SizedBox(height: 16),
                    SizedBox(
                      height: 48,
                      child: OutlinedButton.icon(
                        onPressed: _pickFile,
                        icon: const Icon(Icons.attach_file_rounded),
                        label: Text(_selectedFile != null ? 'Change File' : 'Select File'),
                      ),
                    ),
                    const SizedBox(height: 24),
                    SizedBox(
                      height: 48,
                      child: ElevatedButton(
                        onPressed: _isLoading ? null : _upload,
                        child: _isLoading
                            ? const LoadingIndicator(color: Colors.white)
                            : const Text('Upload'),
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
