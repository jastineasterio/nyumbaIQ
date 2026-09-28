import apiClient from './client';
import type { Floor, FloorFormData, PaginatedResponse } from '../types';

export const floorsApi = {
  getByBuilding: async (buildingId: string, page = 0, size = 20): Promise<PaginatedResponse<Floor>> => {
    const response = await apiClient.get(`/buildings/${buildingId}/floors?page=${page}&size=${size}`);
    return response.data;
  },

  getById: async (id: string): Promise<Floor> => {
    const response = await apiClient.get(`/floors/${id}`);
    return response.data;
  },

  create: async (buildingId: string, data: FloorFormData): Promise<Floor> => {
    const response = await apiClient.post(`/buildings/${buildingId}/floors`, data);
    return response.data;
  },

  update: async (id: string, data: Partial<FloorFormData>): Promise<Floor> => {
    const response = await apiClient.put(`/floors/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/floors/${id}`);
  },
};
