class ReceiptModel {
  final String id;
  final String receiptNumber;
  final String paymentId;
  final String tenantId;
  final double amount;
  final String method;
  final String status;
  final DateTime issuedAt;
  final DateTime createdAt;

  ReceiptModel({
    required this.id,
    required this.receiptNumber,
    required this.paymentId,
    required this.tenantId,
    required this.amount,
    required this.method,
    required this.status,
    required this.issuedAt,
    required this.createdAt,
  });

  factory ReceiptModel.fromJson(Map<String, dynamic> json) {
    return ReceiptModel(
      id: json['id'] as String,
      receiptNumber: json['receiptNumber'] as String,
      paymentId: json['paymentId'] as String,
      tenantId: json['tenantId'] as String,
      amount: (json['amount'] as num).toDouble(),
      method: json['method'] as String,
      status: json['status'] as String,
      issuedAt: DateTime.parse(json['issuedAt'] as String),
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'receiptNumber': receiptNumber,
      'paymentId': paymentId,
      'tenantId': tenantId,
      'amount': amount,
      'method': method,
      'status': status,
      'issuedAt': issuedAt.toIso8601String(),
      'createdAt': createdAt.toIso8601String(),
    };
  }
}
