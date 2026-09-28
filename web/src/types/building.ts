export type BuildingStatus = 'ACTIVE' | 'INACTIVE' | 'UNDER_MAINTENANCE';

export interface Building {
  id: string;
  propertyId: string;
  name: string;
  description?: string;
  floorsCount: number;
  totalUnits?: number;
  status: BuildingStatus;
  createdAt: string;
  updatedAt: string;
}

export interface BuildingFormData {
  propertyId: string;
  name: string;
  description?: string;
  status: BuildingStatus;
}
