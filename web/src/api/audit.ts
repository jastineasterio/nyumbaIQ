import apiClient from './client';
import type { AuditLog, PaginatedResponse } from '../types';

export const auditApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<AuditLog>> => {
    const response = await apiClient.get(`/audit-logs?page=${page}&size=${size}`);
    return response.data;
  },
};
