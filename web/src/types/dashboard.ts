export interface DashboardStats {
  totalProperties: number;
  totalBuildings: number;
  totalFloors: number;
  totalUnits: number;
  occupiedUnits: number;
  vacantUnits: number;
  totalManagers: number;
  totalTenants: number;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  timestamp: string;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  validationErrors?: Record<string, string[]>;
}
