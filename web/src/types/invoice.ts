export type InvoiceStatus = 'DRAFT' | 'SENT' | 'PAID' | 'OVERDUE' | 'CANCELLED';

export interface Invoice {
  id: string;
  invoiceNumber: string;
  tenantId: string;
  tenantName?: string;
  propertyId: string;
  propertyName?: string;
  unitId: string;
  unitName?: string;
  amount: number;
  paidAmount: number;
  balance: number;
  status: InvoiceStatus;
  dueDate: string;
  issuedAt: string;
  paidAt?: string;
}

export interface InvoiceCancelRequest {
  reason: string;
}
