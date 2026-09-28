class FinancialReportModel {
  final String type;
  final double totalIncome;
  final double totalExpense;
  final double net;
  final List<FinancialReportEntryModel> entries;
  final DateTime generatedAt;

  FinancialReportModel({
    required this.type,
    required this.totalIncome,
    required this.totalExpense,
    required this.net,
    required this.entries,
    required this.generatedAt,
  });

  factory FinancialReportModel.fromJson(Map<String, dynamic> json) {
    var entriesList = json['entries'] as List<dynamic>? ?? [];
    final entries = entriesList.map((e) => FinancialReportEntryModel.fromJson(e as Map<String, dynamic>)).toList();

    return FinancialReportModel(
      type: json['type'] as String,
      totalIncome: (json['totalIncome'] as num).toDouble(),
      totalExpense: (json['totalExpense'] as num).toDouble(),
      net: (json['net'] as num).toDouble(),
      entries: entries,
      generatedAt: DateTime.parse(json['generatedAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'type': type,
      'totalIncome': totalIncome,
      'totalExpense': totalExpense,
      'net': net,
      'entries': entries.map((e) => e.toJson()).toList(),
      'generatedAt': generatedAt.toIso8601String(),
    };
  }
}

class FinancialReportEntryModel {
  final String label;
  final double amount;

  FinancialReportEntryModel({
    required this.label,
    required this.amount,
  });

  factory FinancialReportEntryModel.fromJson(Map<String, dynamic> json) {
    return FinancialReportEntryModel(
      label: json['label'] as String,
      amount: (json['amount'] as num).toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'label': label,
      'amount': amount,
    };
  }
}
