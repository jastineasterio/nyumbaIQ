export type TenantStatus = 'ACTIVE' | 'INACTIVE' | 'BLACKLISTED';

export interface Tenant {
  id: string;
  userId: string;
  propertyId: string;
  unitId?: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  idNumber?: string;
  status: TenantStatus;
  leaseStartDate?: string;
  leaseEndDate?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  createdAt: string;
  updatedAt: string;
}

export interface TenantFormData {
  userId?: string;
  propertyId: string;
  unitId?: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  idNumber?: string;
  status: TenantStatus;
  leaseStartDate?: string;
  leaseEndDate?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
}
