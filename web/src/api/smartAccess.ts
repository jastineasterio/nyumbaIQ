import apiClient from './client';
import type { SmartLock, SmartLockCredential, AccessPolicy, SmartLockFormData, CredentialFormData, AccessPolicyFormData, PaginatedResponse } from '../types';

export const smartAccessApi = {
  getLocks: async (page = 0, size = 20): Promise<PaginatedResponse<SmartLock>> => {
    const response = await apiClient.get(`/smart-locks?page=${page}&size=${size}`);
    return response.data;
  },
  getLockById: async (id: string): Promise<SmartLock> => {
    const response = await apiClient.get(`/smart-locks/${id}`);
    return response.data;
  },
  createLock: async (data: SmartLockFormData): Promise<SmartLock> => {
    const response = await apiClient.post('/smart-locks', data);
    return response.data;
  },
  updateLock: async (id: string, data: Partial<SmartLockFormData>): Promise<SmartLock> => {
    const response = await apiClient.put(`/smart-locks/${id}`, data);
    return response.data;
  },
  lock: async (id: string): Promise<void> => {
    await apiClient.post(`/smart-locks/${id}/lock`);
  },
  unlock: async (id: string): Promise<void> => {
    await apiClient.post(`/smart-locks/${id}/unlock`);
  },
  addCredential: async (id: string, data: CredentialFormData): Promise<SmartLockCredential> => {
    const response = await apiClient.post(`/smart-locks/${id}/credentials`, data);
    return response.data;
  },
  getCredentials: async (id: string): Promise<SmartLockCredential[]> => {
    const response = await apiClient.get(`/smart-locks/${id}/credentials`);
    return response.data;
  },
  revokeCredential: async (lockId: string, credentialId: string): Promise<void> => {
    await apiClient.post(`/smart-locks/${lockId}/revoke-credential/${credentialId}`);
  },
  getEvents: async (id: string): Promise<{ id: string; action: string; at: string }[]> => {
    const response = await apiClient.get(`/smart-locks/${id}/events`);
    return response.data;
  },
  getAccessPolicy: async (id: string): Promise<AccessPolicy> => {
    const response = await apiClient.get(`/smart-locks/${id}/access-policy`);
    return response.data;
  },
  updateAccessPolicy: async (id: string, data: AccessPolicyFormData): Promise<AccessPolicy> => {
    const response = await apiClient.post(`/smart-locks/${id}/access-policy`, data);
    return response.data;
  },
};
