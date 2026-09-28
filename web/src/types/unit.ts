export type UnitStatus = 'VACANT' | 'OCCUPIED' | 'RESERVED' | 'UNDER_MAINTENANCE';
export type UnitType = 'STUDIO' | 'ONE_BEDROOM' | 'TWO_BEDROOM' | 'THREE_BEDROOM' | 'PENTHOUSE' | 'COMMERCIAL';

export interface Unit {
  id: string;
  propertyId: string;
  buildingId: string;
  floorId: string;
  unitNumber: string;
  unitType: UnitType;
  status: UnitStatus;
  bedrooms: number;
  bathrooms: number;
  squareFeet?: number;
  monthlyRent: number;
  securityDeposit: number;
  description?: string;
  amenities: string[];
  createdAt: string;
  updatedAt: string;
}

export interface UnitFormData {
  propertyId: string;
  buildingId: string;
  floorId: string;
  unitNumber: string;
  unitType: UnitType;
  bedrooms: number;
  bathrooms: number;
  squareFeet?: number;
  monthlyRent: number;
  securityDeposit: number;
  description?: string;
  amenities: string[];
}
