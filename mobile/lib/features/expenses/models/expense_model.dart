class ExpenseModel {
  final String id;
  final String propertyId;
  final String category;
  final String description;
  final double amount;
  final DateTime expenseDate;
  final String status;
  final String? receiptUrl;
  final String? approvedBy;
  final DateTime? approvedAt;
  final DateTime createdAt;
  final DateTime? updatedAt;

  ExpenseModel({
    required this.id,
    required this.propertyId,
    required this.category,
    required this.description,
    required this.amount,
    required this.expenseDate,
    required this.status,
    this.receiptUrl,
    this.approvedBy,
    this.approvedAt,
    required this.createdAt,
    this.updatedAt,
  });

  factory ExpenseModel.fromJson(Map<String, dynamic> json) {
    return ExpenseModel(
      id: json['id'] as String,
      propertyId: json['propertyId'] as String,
      category: json['category'] as String,
      description: json['description'] as String,
      amount: (json['amount'] as num).toDouble(),
      expenseDate: DateTime.parse(json['expenseDate'] as String),
      status: json['status'] as String,
      receiptUrl: json['receiptUrl'] as String?,
      approvedBy: json['approvedBy'] as String?,
      approvedAt: json['approvedAt'] != null ? DateTime.parse(json['approvedAt'] as String) : null,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt'] as String) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'propertyId': propertyId,
      'category': category,
      'description': description,
      'amount': amount,
      'expenseDate': expenseDate.toIso8601String(),
      'status': status,
      'receiptUrl': receiptUrl,
      'approvedBy': approvedBy,
      'approvedAt': approvedAt?.toIso8601String(),
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }
}
