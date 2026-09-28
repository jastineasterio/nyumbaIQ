import apiClient from './client';
import type { Tenant, TenantFormData, PaginatedResponse } from '../types';

export const tenantsApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<Tenant>> => {
    const response = await apiClient.get(`/tenants?page=${page}&size=${size}`);
    return response.data;
  },

  getById: async (id: string): Promise<Tenant> => {
    const response = await apiClient.get(`/tenants/${id}`);
    return response.data;
  },

  create: async (data: TenantFormData): Promise<Tenant> => {
    const response = await apiClient.post('/tenants', data);
    return response.data;
  },

  update: async (id: string, data: Partial<TenantFormData>): Promise<Tenant> => {
    const response = await apiClient.put(`/tenants/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/tenants/${id}`);
  },
};
