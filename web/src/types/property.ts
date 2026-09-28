export type PropertyStatus = 'ACTIVE' | 'INACTIVE' | 'UNDER_MAINTENANCE';

export interface Property {
  id: string;
  name: string;
  description?: string;
  address: string;
  city: string;
  state: string;
  country: string;
  postalCode?: string;
  status: PropertyStatus;
  totalBuildings?: number;
  totalUnits?: number;
  createdAt: string;
  updatedAt: string;
}

export interface PropertyFormData {
  name: string;
  description?: string;
  address: string;
  city: string;
  state: string;
  country: string;
  postalCode?: string;
  status: PropertyStatus;
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}
