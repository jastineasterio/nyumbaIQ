import apiClient from './client';
import type { Unit, UnitFormData, PaginatedResponse } from '../types';

export const unitsApi = {
  getByFloor: async (floorId: string, page = 0, size = 20): Promise<PaginatedResponse<Unit>> => {
    const response = await apiClient.get(`/floors/${floorId}/units?page=${page}&size=${size}`);
    return response.data;
  },

  getAll: async (params: {
    propertyId?: string;
    status?: string;
    page?: number;
    size?: number;
  }): Promise<PaginatedResponse<Unit>> => {
    const { propertyId, status, page = 0, size = 20 } = params;
    const query = new URLSearchParams();
    if (propertyId) query.append('propertyId', propertyId);
    if (status) query.append('status', status);
    query.append('page', String(page));
    query.append('size', String(size));
    const response = await apiClient.get(`/units?${query.toString()}`);
    return response.data;
  },

  getById: async (id: string): Promise<Unit> => {
    const response = await apiClient.get(`/units/${id}`);
    return response.data;
  },

  create: async (floorId: string, data: UnitFormData): Promise<Unit> => {
    const response = await apiClient.post(`/floors/${floorId}/units`, data);
    return response.data;
  },

  update: async (id: string, data: Partial<UnitFormData>): Promise<Unit> => {
    const response = await apiClient.put(`/units/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/units/${id}`);
  },
};
