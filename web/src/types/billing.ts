export interface BillingSchedule {
  id: string;
  name: string;
  propertyId: string;
  propertyName?: string;
  frequency: 'MONTHLY' | 'QUARTERLY' | 'ANNUALLY';
  amount: number;
  dueDay: number;
  isActive: boolean;
  createdAt: string;
}

export interface BillingGenerateRequest {
  propertyId: string;
  scheduleId?: string;
  month?: string;
}
