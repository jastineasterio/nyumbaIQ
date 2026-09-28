import apiClient from './client';
import type { Payment, PaymentAllocateRequest, PaymentVerifyRequest, MobileMoneyWebhookPayload, PaginatedResponse } from '../types';

export const paymentsApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<Payment>> => {
    const response = await apiClient.get(`/payments?page=${page}&size=${size}`);
    return response.data;
  },
  getById: async (id: string): Promise<Payment> => {
    const response = await apiClient.get(`/payments/${id}`);
    return response.data;
  },
  getByReference: async (reference: string): Promise<Payment> => {
    const response = await apiClient.get(`/payments/reference/${reference}`);
    return response.data;
  },
  create: async (data: Partial<Payment>): Promise<Payment> => {
    const response = await apiClient.post('/payments', data);
    return response.data;
  },
  allocate: async (id: string, data: PaymentAllocateRequest): Promise<Payment> => {
    const response = await apiClient.post(`/payments/${id}/allocate`, data);
    return response.data;
  },
  verify: async (data: PaymentVerifyRequest): Promise<Payment> => {
    const response = await apiClient.post('/payments/verify', data);
    return response.data;
  },
  mobileMoneyWebhook: async (payload: MobileMoneyWebhookPayload): Promise<void> => {
    await apiClient.post('/payments/webhook/mobile-money', payload);
  },
};
