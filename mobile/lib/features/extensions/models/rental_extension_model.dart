class RentalExtensionModel {
  final String id;
  final String leaseId;
  final String tenantId;
  final DateTime proposedEndDate;
  final String status;
  final String? notes;
  final DateTime createdAt;
  final DateTime? updatedAt;

  RentalExtensionModel({
    required this.id,
    required this.leaseId,
    required this.tenantId,
    required this.proposedEndDate,
    required this.status,
    this.notes,
    required this.createdAt,
    this.updatedAt,
  });

  factory RentalExtensionModel.fromJson(Map<String, dynamic> json) {
    return RentalExtensionModel(
      id: json['id'] as String,
      leaseId: json['leaseId'] as String,
      tenantId: json['tenantId'] as String,
      proposedEndDate: DateTime.parse(json['proposedEndDate'] as String),
      status: json['status'] as String,
      notes: json['notes'] as String?,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt'] as String) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'leaseId': leaseId,
      'tenantId': tenantId,
      'proposedEndDate': proposedEndDate.toIso8601String(),
      'status': status,
      'notes': notes,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }
}
