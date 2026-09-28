class SmartLockModel {
  final String id;
  final String unitId;
  final String name;
  final String model;
  final String status;
  final bool isLocked;
  final DateTime? lastConnected;
  final DateTime createdAt;

  SmartLockModel({
    required this.id,
    required this.unitId,
    required this.name,
    required this.model,
    required this.status,
    required this.isLocked,
    this.lastConnected,
    required this.createdAt,
  });

  factory SmartLockModel.fromJson(Map<String, dynamic> json) {
    return SmartLockModel(
      id: json['id'] as String,
      unitId: json['unitId'] as String,
      name: json['name'] as String,
      model: json['model'] as String,
      status: json['status'] as String,
      isLocked: json['isLocked'] as bool,
      lastConnected: json['lastConnected'] != null ? DateTime.parse(json['lastConnected'] as String) : null,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'unitId': unitId,
      'name': name,
      'model': model,
      'status': status,
      'isLocked': isLocked,
      'lastConnected': lastConnected?.toIso8601String(),
      'createdAt': createdAt.toIso8601String(),
    };
  }
}

class AccessCredentialModel {
  final String id;
  final String lockId;
  final String userId;
  final String type;
  final String code;
  final bool isActive;
  final DateTime? expiresAt;
  final DateTime createdAt;

  AccessCredentialModel({
    required this.id,
    required this.lockId,
    required this.userId,
    required this.type,
    required this.code,
    required this.isActive,
    this.expiresAt,
    required this.createdAt,
  });

  factory AccessCredentialModel.fromJson(Map<String, dynamic> json) {
    return AccessCredentialModel(
      id: json['id'] as String,
      lockId: json['lockId'] as String,
      userId: json['userId'] as String,
      type: json['type'] as String,
      code: json['code'] as String,
      isActive: json['isActive'] as bool,
      expiresAt: json['expiresAt'] != null ? DateTime.parse(json['expiresAt'] as String) : null,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'lockId': lockId,
      'userId': userId,
      'type': type,
      'code': code,
      'isActive': isActive,
      'expiresAt': expiresAt?.toIso8601String(),
      'createdAt': createdAt.toIso8601String(),
    };
  }
}

class AccessEventModel {
  final String id;
  final String lockId;
  final String userId;
  final String eventType;
  final DateTime occurredAt;

  AccessEventModel({
    required this.id,
    required this.lockId,
    required this.userId,
    required this.eventType,
    required this.occurredAt,
  });

  factory AccessEventModel.fromJson(Map<String, dynamic> json) {
    return AccessEventModel(
      id: json['id'] as String,
      lockId: json['lockId'] as String,
      userId: json['userId'] as String,
      eventType: json['eventType'] as String,
      occurredAt: DateTime.parse(json['occurredAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'lockId': lockId,
      'userId': userId,
      'eventType': eventType,
      'occurredAt': occurredAt.toIso8601String(),
    };
  }
}
