export interface Lease {
  id: string;
  leaseNumber: string;
  propertyId: string;
  propertyName?: string;
  unitId: string;
  unitName?: string;
  tenantId: string;
  tenantName?: string;
  startDate: string;
  endDate: string;
  rentAmount: number;
  depositAmount: number;
  status: 'ACTIVE' | 'PENDING' | 'EXPIRED' | 'TERMINATED';
  createdAt: string;
  updatedAt: string;
}

export interface LeaseFormData {
  propertyId: string;
  unitId: string;
  tenantId: string;
  startDate: string;
  endDate: string;
  rentAmount: number;
  depositAmount: number;
}
