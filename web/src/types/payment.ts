export interface Payment {
  id: string;
  reference: string;
  tenantId: string;
  tenantName?: string;
  propertyId: string;
  propertyName?: string;
  unitId: string;
  unitName?: string;
  amount: number;
  method: 'MOBILE_MONEY' | 'BANK_TRANSFER' | 'CASH' | 'CHEQUE';
  status: 'PENDING' | 'COMPLETED' | 'FAILED' | 'REFUNDED';
  paidAt: string;
  createdAt: string;
}

export interface PaymentAllocateRequest {
  invoiceId: string;
  amount: number;
}

export interface PaymentVerifyRequest {
  reference: string;
}

export interface MobileMoneyWebhookPayload {
  reference: string;
  amount: number;
  status: string;
  transactionId: string;
}
