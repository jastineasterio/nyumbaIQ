class InvoiceModel {
  final String id;
  final String invoiceNumber;
  final String leaseId;
  final String tenantId;
  final double totalAmount;
  final double paidAmount;
  final double balance;
  final String status;
  final DateTime dueDate;
  final DateTime? paidDate;
  final List<InvoiceItemModel> items;
  final DateTime createdAt;
  final DateTime? updatedAt;

  InvoiceModel({
    required this.id,
    required this.invoiceNumber,
    required this.leaseId,
    required this.tenantId,
    required this.totalAmount,
    required this.paidAmount,
    required this.balance,
    required this.status,
    required this.dueDate,
    this.paidDate,
    required this.items,
    required this.createdAt,
    this.updatedAt,
  });

  factory InvoiceModel.fromJson(Map<String, dynamic> json) {
    var itemsList = json['items'] as List<dynamic>? ?? [];
    final items = itemsList.map((e) => InvoiceItemModel.fromJson(e as Map<String, dynamic>)).toList();

    return InvoiceModel(
      id: json['id'] as String,
      invoiceNumber: json['invoiceNumber'] as String,
      leaseId: json['leaseId'] as String,
      tenantId: json['tenantId'] as String,
      totalAmount: (json['totalAmount'] as num).toDouble(),
      paidAmount: (json['paidAmount'] as num).toDouble(),
      balance: (json['balance'] as num).toDouble(),
      status: json['status'] as String,
      dueDate: DateTime.parse(json['dueDate'] as String),
      paidDate: json['paidDate'] != null ? DateTime.parse(json['paidDate'] as String) : null,
      items: items,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt'] as String) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'invoiceNumber': invoiceNumber,
      'leaseId': leaseId,
      'tenantId': tenantId,
      'totalAmount': totalAmount,
      'paidAmount': paidAmount,
      'balance': balance,
      'status': status,
      'dueDate': dueDate.toIso8601String(),
      'paidDate': paidDate?.toIso8601String(),
      'items': items.map((e) => e.toJson()).toList(),
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }
}

class InvoiceItemModel {
  final String description;
  final double amount;
  final String? category;

  InvoiceItemModel({
    required this.description,
    required this.amount,
    this.category,
  });

  factory InvoiceItemModel.fromJson(Map<String, dynamic> json) {
    return InvoiceItemModel(
      description: json['description'] as String,
      amount: (json['amount'] as num).toDouble(),
      category: json['category'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'description': description,
      'amount': amount,
      'category': category,
    };
  }
}
