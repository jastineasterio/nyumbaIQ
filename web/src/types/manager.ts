export type ManagerStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';

export interface Manager {
  id: string;
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  status: ManagerStatus;
  propertyIds: string[];
  createdAt: string;
  updatedAt: string;
}

export interface ManagerFormData {
  userId?: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  status: ManagerStatus;
}

export interface PropertyAssignment {
  id: string;
  managerId: string;
  propertyId: string;
  propertyName: string;
  assignedAt: string;
}
