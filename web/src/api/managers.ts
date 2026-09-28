import apiClient from './client';
import type { Manager, ManagerFormData, PaginatedResponse, PropertyAssignment } from '../types';

export const managersApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<Manager>> => {
    const response = await apiClient.get(`/managers?page=${page}&size=${size}`);
    return response.data;
  },

  getById: async (id: string): Promise<Manager> => {
    const response = await apiClient.get(`/managers/${id}`);
    return response.data;
  },

  create: async (data: ManagerFormData): Promise<Manager> => {
    const response = await apiClient.post('/managers', data);
    return response.data;
  },

  update: async (id: string, data: Partial<ManagerFormData>): Promise<Manager> => {
    const response = await apiClient.put(`/managers/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/managers/${id}`);
  },

  assignProperty: async (managerId: string, propertyId: string): Promise<PropertyAssignment> => {
    const response = await apiClient.post(`/managers/${managerId}/assign-property`, { propertyId });
    return response.data;
  },

  unassignProperty: async (managerId: string, assignmentId: string): Promise<void> => {
    await apiClient.delete(`/managers/${managerId}/assignments/${assignmentId}`);
  },
};
