import 'package:flutter/material.dart';
import '../../../core/widgets/custom_app_bar.dart';

class ReceiptsListScreen extends StatelessWidget {
  const ReceiptsListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      appBar: CustomAppBar(title: 'Receipts'),
      body: Center(child: Text('Receipts - coming soon')),
    );
  }
}
