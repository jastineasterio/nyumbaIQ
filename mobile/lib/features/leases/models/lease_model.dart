class LeaseModel {
  final String id;
  final String unitId;
  final String tenantId;
  final DateTime startDate;
  final DateTime endDate;
  final double rentAmount;
  final double depositAmount;
  final String status;
  final String? notes;
  final DateTime createdAt;
  final DateTime? updatedAt;

  LeaseModel({
    required this.id,
    required this.unitId,
    required this.tenantId,
    required this.startDate,
    required this.endDate,
    required this.rentAmount,
    required this.depositAmount,
    required this.status,
    this.notes,
    required this.createdAt,
    this.updatedAt,
  });

  factory LeaseModel.fromJson(Map<String, dynamic> json) {
    return LeaseModel(
      id: json['id'] as String,
      unitId: json['unitId'] as String,
      tenantId: json['tenantId'] as String,
      startDate: DateTime.parse(json['startDate'] as String),
      endDate: DateTime.parse(json['endDate'] as String),
      rentAmount: (json['rentAmount'] as num).toDouble(),
      depositAmount: (json['depositAmount'] as num).toDouble(),
      status: json['status'] as String,
      notes: json['notes'] as String?,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt'] as String) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'unitId': unitId,
      'tenantId': tenantId,
      'startDate': startDate.toIso8601String(),
      'endDate': endDate.toIso8601String(),
      'rentAmount': rentAmount,
      'depositAmount': depositAmount,
      'status': status,
      'notes': notes,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }
}
