class BillingScheduleModel {
  final String id;
  final String unitId;
  final String leaseId;
  final double amount;
  final String frequency;
  final DateTime dueDate;
  final String status;
  final DateTime createdAt;

  BillingScheduleModel({
    required this.id,
    required this.unitId,
    required this.leaseId,
    required this.amount,
    required this.frequency,
    required this.dueDate,
    required this.status,
    required this.createdAt,
  });

  factory BillingScheduleModel.fromJson(Map<String, dynamic> json) {
    return BillingScheduleModel(
      id: json['id'] as String,
      unitId: json['unitId'] as String,
      leaseId: json['leaseId'] as String,
      amount: (json['amount'] as num).toDouble(),
      frequency: json['frequency'] as String,
      dueDate: DateTime.parse(json['dueDate'] as String),
      status: json['status'] as String,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'unitId': unitId,
      'leaseId': leaseId,
      'amount': amount,
      'frequency': frequency,
      'dueDate': dueDate.toIso8601String(),
      'status': status,
      'createdAt': createdAt.toIso8601String(),
    };
  }
}
