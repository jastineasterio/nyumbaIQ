class MaintenanceRequestModel {
  final String id;
  final String unitId;
  final String tenantId;
  final String title;
  final String description;
  final String priority;
  final String status;
  final String? assignedTo;
  final DateTime? scheduledDate;
  final List<MaintenanceCostModel> costs;
  final DateTime createdAt;
  final DateTime? updatedAt;

  MaintenanceRequestModel({
    required this.id,
    required this.unitId,
    required this.tenantId,
    required this.title,
    required this.description,
    required this.priority,
    required this.status,
    this.assignedTo,
    this.scheduledDate,
    required this.costs,
    required this.createdAt,
    this.updatedAt,
  });

  factory MaintenanceRequestModel.fromJson(Map<String, dynamic> json) {
    var costsList = json['costs'] as List<dynamic>? ?? [];
    final costs = costsList.map((e) => MaintenanceCostModel.fromJson(e as Map<String, dynamic>)).toList();

    return MaintenanceRequestModel(
      id: json['id'] as String,
      unitId: json['unitId'] as String,
      tenantId: json['tenantId'] as String,
      title: json['title'] as String,
      description: json['description'] as String,
      priority: json['priority'] as String,
      status: json['status'] as String,
      assignedTo: json['assignedTo'] as String?,
      scheduledDate: json['scheduledDate'] != null ? DateTime.parse(json['scheduledDate'] as String) : null,
      costs: costs,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt'] as String) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'unitId': unitId,
      'tenantId': tenantId,
      'title': title,
      'description': description,
      'priority': priority,
      'status': status,
      'assignedTo': assignedTo,
      'scheduledDate': scheduledDate?.toIso8601String(),
      'costs': costs.map((e) => e.toJson()).toList(),
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }
}

class MaintenanceCostModel {
  final String id;
  final String description;
  final double amount;
  final String category;
  final DateTime createdAt;

  MaintenanceCostModel({
    required this.id,
    required this.description,
    required this.amount,
    required this.category,
    required this.createdAt,
  });

  factory MaintenanceCostModel.fromJson(Map<String, dynamic> json) {
    return MaintenanceCostModel(
      id: json['id'] as String,
      description: json['description'] as String,
      amount: (json['amount'] as num).toDouble(),
      category: json['category'] as String,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'description': description,
      'amount': amount,
      'category': category,
      'createdAt': createdAt.toIso8601String(),
    };
  }
}
