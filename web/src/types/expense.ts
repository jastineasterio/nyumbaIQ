export type ExpenseStatus = 'DRAFT' | 'SUBMITTED' | 'APPROVED' | 'PAID' | 'CANCELLED';

export interface Expense {
  id: string;
  propertyId: string;
  propertyName?: string;
  category: string;
  description: string;
  amount: number;
  status: ExpenseStatus;
  approvedBy?: string;
  paidAt?: string;
  createdAt: string;
  updatedAt: string;
}

export interface ExpenseFormData {
  propertyId: string;
  category: string;
  description: string;
  amount: number;
}
