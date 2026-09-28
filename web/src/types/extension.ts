export type ExtensionStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';

export interface Extension {
  id: string;
  leaseId: string;
  leaseNumber?: string;
  tenantId: string;
  tenantName?: string;
  currentEndDate: string;
  requestedEndDate: string;
  reason: string;
  status: ExtensionStatus;
  reviewedBy?: string;
  reviewedAt?: string;
  createdAt: string;
}

export interface ExtensionFormData {
  leaseId: string;
  currentEndDate: string;
  requestedEndDate: string;
  reason: string;
}
