import 'package:flutter/material.dart';
import '../../../core/widgets/custom_app_bar.dart';
import '../../../core/widgets/loading_indicator.dart';

class StatementDetailScreen extends StatelessWidget {
  final String statementId;

  const StatementDetailScreen({super.key, required this.statementId});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const CustomAppBar(title: 'Statement Details'),
      body: Center(
        child: Text('Statement detail for $statementId - coming soon'),
      ),
    );
  }
}
