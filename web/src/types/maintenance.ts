export type MaintenanceStatus = 'OPEN' | 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
export type MaintenancePriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT';

export interface MaintenanceRequest {
  id: string;
  propertyId: string;
  propertyName?: string;
  unitId: string;
  unitName?: string;
  reportedBy: string;
  reporterName?: string;
  title: string;
  description: string;
  priority: MaintenancePriority;
  status: MaintenanceStatus;
  assignedTo?: string;
  assigneeName?: string;
  costs: MaintenanceCost[];
  createdAt: string;
  updatedAt: string;
}

export interface MaintenanceCost {
  id: string;
  description: string;
  amount: number;
  createdAt: string;
}

export interface MaintenanceFormData {
  propertyId: string;
  unitId: string;
  title: string;
  description: string;
  priority: MaintenancePriority;
}

export interface MaintenanceAssignData {
  assigneeId: string;
}

export interface MaintenanceStatusData {
  status: MaintenanceStatus;
}

export interface MaintenanceCostData {
  description: string;
  amount: number;
}
