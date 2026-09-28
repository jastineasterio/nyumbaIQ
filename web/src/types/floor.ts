export type FloorStatus = 'ACTIVE' | 'INACTIVE' | 'UNDER_MAINTENANCE';

export interface Floor {
  id: string;
  buildingId: string;
  propertyId: string;
  level: number;
  name?: string;
  unitsCount: number;
  status: FloorStatus;
  createdAt: string;
  updatedAt: string;
}

export interface FloorFormData {
  buildingId: string;
  propertyId: string;
  level: number;
  name?: string;
  status: FloorStatus;
}
