class StatementModel {
  final String id;
  final String statementNumber;
  final String leaseId;
  final String tenantId;
  final DateTime startDate;
  final DateTime endDate;
  final double openingBalance;
  final double closingBalance;
  final List<StatementEntryModel> entries;
  final DateTime createdAt;

  StatementModel({
    required this.id,
    required this.statementNumber,
    required this.leaseId,
    required this.tenantId,
    required this.startDate,
    required this.endDate,
    required this.openingBalance,
    required this.closingBalance,
    required this.entries,
    required this.createdAt,
  });

  factory StatementModel.fromJson(Map<String, dynamic> json) {
    var entriesList = json['entries'] as List<dynamic>? ?? [];
    final entries = entriesList.map((e) => StatementEntryModel.fromJson(e as Map<String, dynamic>)).toList();

    return StatementModel(
      id: json['id'] as String,
      statementNumber: json['statementNumber'] as String,
      leaseId: json['leaseId'] as String,
      tenantId: json['tenantId'] as String,
      startDate: DateTime.parse(json['startDate'] as String),
      endDate: DateTime.parse(json['endDate'] as String),
      openingBalance: (json['openingBalance'] as num).toDouble(),
      closingBalance: (json['closingBalance'] as num).toDouble(),
      entries: entries,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'statementNumber': statementNumber,
      'leaseId': leaseId,
      'tenantId': tenantId,
      'startDate': startDate.toIso8601String(),
      'endDate': endDate.toIso8601String(),
      'openingBalance': openingBalance,
      'closingBalance': closingBalance,
      'entries': entries.map((e) => e.toJson()).toList(),
      'createdAt': createdAt.toIso8601String(),
    };
  }
}

class StatementEntryModel {
  final String date;
  final String description;
  final double amount;
  final String type;

  StatementEntryModel({
    required this.date,
    required this.description,
    required this.amount,
    required this.type,
  });

  factory StatementEntryModel.fromJson(Map<String, dynamic> json) {
    return StatementEntryModel(
      date: json['date'] as String,
      description: json['description'] as String,
      amount: (json['amount'] as num).toDouble(),
      type: json['type'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'date': date,
      'description': description,
      'amount': amount,
      'type': type,
    };
  }
}
