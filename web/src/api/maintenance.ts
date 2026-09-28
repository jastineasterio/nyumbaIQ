import apiClient from './client';
import type { MaintenanceRequest, MaintenanceFormData, MaintenanceAssignData, MaintenanceStatusData, MaintenanceCostData, PaginatedResponse } from '../types';

export const maintenanceApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<MaintenanceRequest>> => {
    const response = await apiClient.get(`/maintenance?page=${page}&size=${size}`);
    return response.data;
  },
  getById: async (id: string): Promise<MaintenanceRequest> => {
    const response = await apiClient.get(`/maintenance/${id}`);
    return response.data;
  },
  create: async (data: MaintenanceFormData): Promise<MaintenanceRequest> => {
    const response = await apiClient.post('/maintenance', data);
    return response.data;
  },
  update: async (id: string, data: Partial<MaintenanceFormData>): Promise<MaintenanceRequest> => {
    const response = await apiClient.put(`/maintenance/${id}`, data);
    return response.data;
  },
  assign: async (id: string, data: MaintenanceAssignData): Promise<MaintenanceRequest> => {
    const response = await apiClient.post(`/maintenance/${id}/assign`, data);
    return response.data;
  },
  updateStatus: async (id: string, data: MaintenanceStatusData): Promise<MaintenanceRequest> => {
    const response = await apiClient.post(`/maintenance/${id}/status`, data);
    return response.data;
  },
  addCost: async (id: string, data: MaintenanceCostData): Promise<MaintenanceRequest> => {
    const response = await apiClient.post(`/maintenance/${id}/costs`, data);
    return response.data;
  },
  getCosts: async (id: string): Promise<MaintenanceRequest['costs']> => {
    const response = await apiClient.get(`/maintenance/${id}/costs`);
    return response.data;
  },
};
