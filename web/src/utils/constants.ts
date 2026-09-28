export const USER_ROLES = {
  OWNER: 'OWNER',
  MANAGER: 'MANAGER',
  TENANT: 'TENANT',
} as const;

export const USER_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  SUSPENDED: 'SUSPENDED',
} as const;

export const PROPERTY_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  UNDER_MAINTENANCE: 'UNDER_MAINTENANCE',
} as const;

export const UNIT_STATUS = {
  VACANT: 'VACANT',
  OCCUPIED: 'OCCUPIED',
  RESERVED: 'RESERVED',
  UNDER_MAINTENANCE: 'UNDER_MAINTENANCE',
} as const;

export const UNIT_TYPES = {
  STUDIO: 'STUDIO',
  ONE_BEDROOM: 'ONE_BEDROOM',
  TWO_BEDROOM: 'TWO_BEDROOM',
  THREE_BEDROOM: 'THREE_BEDROOM',
  PENTHOUSE: 'PENTHOUSE',
  COMMERCIAL: 'COMMERCIAL',
} as const;

export const TENANT_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  BLACKLISTED: 'BLACKLISTED',
} as const;

export const MANAGER_STATUS = {
  ACTIVE: 'ACTIVE',
  INACTIVE: 'INACTIVE',
  SUSPENDED: 'SUSPENDED',
} as const;

export const DOCUMENT_STATUS = {
  PENDING: 'PENDING',
  APPROVED: 'APPROVED',
  REJECTED: 'REJECTED',
} as const;

export const AUDIT_ACTIONS = {
  CREATE: 'CREATE',
  UPDATE: 'UPDATE',
  DELETE: 'DELETE',
  LOGIN: 'LOGIN',
  LOGOUT: 'LOGOUT',
  ASSIGN: 'ASSIGN',
  REVIEW: 'REVIEW',
  OTHER: 'OTHER',
} as const;

export const ROUTES = {
  LOGIN: '/login',
  DASHBOARD: '/dashboard',
  PROPERTIES: '/properties',
  PROPERTY_DETAILS: '/properties/:id',
  BUILDINGS: '/buildings',
  FLOORS: '/floors',
  UNITS: '/units',
  TENANTS: '/tenants',
  MANAGERS: '/managers',
  KYC: '/kyc',
  AUDIT_LOGS: '/audit-logs',
  PROFILE: '/profile',
} as const;
