import 'package:flutter/material.dart';
import '../../../core/widgets/custom_app_bar.dart';

class StatementsListScreen extends StatelessWidget {
  const StatementsListScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      appBar: CustomAppBar(title: 'Statements'),
      body: Center(child: Text('Statements - coming soon')),
    );
  }
}
