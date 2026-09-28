import apiClient from './client';
import type { Building, BuildingFormData, PaginatedResponse } from '../types';

export const buildingsApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<Building>> => {
    const response = await apiClient.get(`/buildings?page=${page}&size=${size}`);
    return response.data;
  },

  getByProperty: async (propertyId: string, page = 0, size = 20): Promise<PaginatedResponse<Building>> => {
    const response = await apiClient.get(`/properties/${propertyId}/buildings?page=${page}&size=${size}`);
    return response.data;
  },

  getById: async (id: string): Promise<Building> => {
    const response = await apiClient.get(`/buildings/${id}`);
    return response.data;
  },

  create: async (propertyId: string, data: BuildingFormData): Promise<Building> => {
    const response = await apiClient.post(`/properties/${propertyId}/buildings`, data);
    return response.data;
  },

  update: async (id: string, data: Partial<BuildingFormData>): Promise<Building> => {
    const response = await apiClient.put(`/buildings/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/buildings/${id}`);
  },
};
