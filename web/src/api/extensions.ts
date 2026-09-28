import apiClient from './client';
import type { Extension, ExtensionFormData, PaginatedResponse } from '../types';

export const extensionsApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<Extension>> => {
    const response = await apiClient.get(`/extensions?page=${page}&size=${size}`);
    return response.data;
  },
  getById: async (id: string): Promise<Extension> => {
    const response = await apiClient.get(`/extensions/${id}`);
    return response.data;
  },
  create: async (data: ExtensionFormData): Promise<Extension> => {
    const response = await apiClient.post('/extensions', data);
    return response.data;
  },
  approve: async (id: string): Promise<Extension> => {
    const response = await apiClient.post(`/extensions/${id}/approve`);
    return response.data;
  },
  reject: async (id: string): Promise<Extension> => {
    const response = await apiClient.post(`/extensions/${id}/reject`);
    return response.data;
  },
  cancel: async (id: string): Promise<Extension> => {
    const response = await apiClient.post(`/extensions/${id}/cancel`);
    return response.data;
  },
};
