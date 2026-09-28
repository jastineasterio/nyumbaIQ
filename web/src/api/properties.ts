import apiClient from './client';
import type { Property, PropertyFormData, PaginatedResponse } from '../types';

export const propertiesApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<Property>> => {
    const response = await apiClient.get(`/properties?page=${page}&size=${size}`);
    return response.data;
  },

  getById: async (id: string): Promise<Property> => {
    const response = await apiClient.get(`/properties/${id}`);
    return response.data;
  },

  create: async (data: PropertyFormData): Promise<Property> => {
    const response = await apiClient.post('/properties', data);
    return response.data;
  },

  update: async (id: string, data: Partial<PropertyFormData>): Promise<Property> => {
    const response = await apiClient.put(`/properties/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/properties/${id}`);
  },
};
