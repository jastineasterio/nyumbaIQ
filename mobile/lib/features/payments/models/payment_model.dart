class PaymentModel {
  final String id;
  final String reference;
  final String invoiceId;
  final String tenantId;
  final double amount;
  final String method;
  final String status;
  final String? transactionId;
  final DateTime? paidAt;
  final List<PaymentAllocationModel> allocations;
  final DateTime createdAt;

  PaymentModel({
    required this.id,
    required this.reference,
    required this.invoiceId,
    required this.tenantId,
    required this.amount,
    required this.method,
    required this.status,
    this.transactionId,
    this.paidAt,
    required this.allocations,
    required this.createdAt,
  });

  factory PaymentModel.fromJson(Map<String, dynamic> json) {
    var allocList = json['allocations'] as List<dynamic>? ?? [];
    final allocations = allocList.map((e) => PaymentAllocationModel.fromJson(e as Map<String, dynamic>)).toList();

    return PaymentModel(
      id: json['id'] as String,
      reference: json['reference'] as String,
      invoiceId: json['invoiceId'] as String,
      tenantId: json['tenantId'] as String,
      amount: (json['amount'] as num).toDouble(),
      method: json['method'] as String,
      status: json['status'] as String,
      transactionId: json['transactionId'] as String?,
      paidAt: json['paidAt'] != null ? DateTime.parse(json['paidAt'] as String) : null,
      allocations: allocations,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'reference': reference,
      'invoiceId': invoiceId,
      'tenantId': tenantId,
      'amount': amount,
      'method': method,
      'status': status,
      'transactionId': transactionId,
      'paidAt': paidAt?.toIso8601String(),
      'allocations': allocations.map((e) => e.toJson()).toList(),
      'createdAt': createdAt.toIso8601String(),
    };
  }
}

class PaymentAllocationModel {
  final String invoiceId;
  final double amount;

  PaymentAllocationModel({
    required this.invoiceId,
    required this.amount,
  });

  factory PaymentAllocationModel.fromJson(Map<String, dynamic> json) {
    return PaymentAllocationModel(
      invoiceId: json['invoiceId'] as String,
      amount: (json['amount'] as num).toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'invoiceId': invoiceId,
      'amount': amount,
    };
  }
}
