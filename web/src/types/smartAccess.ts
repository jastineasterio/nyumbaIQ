export type LockStatus = 'LOCKED' | 'UNLOCKED' | 'ONLINE' | 'OFFLINE';

export interface SmartLock {
  id: string;
  propertyId: string;
  propertyName?: string;
  unitId: string;
  unitName?: string;
  name: string;
  model: string;
  status: LockStatus;
  lastEventAt?: string;
  createdAt: string;
}

export interface SmartLockCredential {
  id: string;
  lockId: string;
  type: 'PASSWORD' | 'CARD' | 'FINGERPRINT' | 'APP';
  name: string;
  createdAt: string;
  expiresAt?: string;
}

export interface AccessPolicy {
  id: string;
  lockId: string;
  name: string;
  allowedDays: string[];
  startTime: string;
  endTime: string;
  isActive: boolean;
}

export interface SmartLockFormData {
  propertyId: string;
  unitId: string;
  name: string;
  model: string;
}

export interface CredentialFormData {
  type: 'PASSWORD' | 'CARD' | 'FINGERPRINT' | 'APP';
  name: string;
  expiresAt?: string;
}

export interface AccessPolicyFormData {
  name: string;
  allowedDays: string[];
  startTime: string;
  endTime: string;
  isActive: boolean;
}
