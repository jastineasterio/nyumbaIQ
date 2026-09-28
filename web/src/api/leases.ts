import apiClient from './client';
import type { Lease, LeaseFormData, PaginatedResponse } from '../types';

export const leasesApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<Lease>> => {
    const response = await apiClient.get(`/leases?page=${page}&size=${size}`);
    return response.data;
  },
  getById: async (id: string): Promise<Lease> => {
    const response = await apiClient.get(`/leases/${id}`);
    return response.data;
  },
  getByNumber: async (leaseNumber: string): Promise<Lease> => {
    const response = await apiClient.get(`/leases/number/${leaseNumber}`);
    return response.data;
  },
  create: async (data: LeaseFormData): Promise<Lease> => {
    const response = await apiClient.post('/leases', data);
    return response.data;
  },
  update: async (id: string, data: Partial<LeaseFormData>): Promise<Lease> => {
    const response = await apiClient.put(`/leases/${id}`, data);
    return response.data;
  },
  renew: async (id: string, endDate: string): Promise<Lease> => {
    const response = await apiClient.post(`/leases/${id}/renew`, { endDate });
    return response.data;
  },
  approve: async (id: string): Promise<Lease> => {
    const response = await apiClient.post(`/leases/${id}/approve`);
    return response.data;
  },
  terminate: async (id: string): Promise<Lease> => {
    const response = await apiClient.post(`/leases/${id}/terminate`);
    return response.data;
  },
};
