import apiClient from './client';
import type { BillingSchedule, BillingGenerateRequest } from '../types';

export const billingApi = {
  generate: async (data: BillingGenerateRequest): Promise<void> => {
    await apiClient.post('/billing/generate', data);
  },
  getSchedules: async (): Promise<BillingSchedule[]> => {
    const response = await apiClient.get('/billing/schedules');
    return response.data;
  },
  getScheduleById: async (id: string): Promise<BillingSchedule> => {
    const response = await apiClient.get(`/billing/schedules/${id}`);
    return response.data;
  },
};
