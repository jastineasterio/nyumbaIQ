import apiClient from './client';
import type { Invoice, PaginatedResponse, InvoiceCancelRequest } from '../types';

export const invoicesApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<Invoice>> => {
    const response = await apiClient.get(`/invoices?page=${page}&size=${size}`);
    return response.data;
  },
  getById: async (id: string): Promise<Invoice> => {
    const response = await apiClient.get(`/invoices/${id}`);
    return response.data;
  },
  getByNumber: async (invoiceNumber: string): Promise<Invoice> => {
    const response = await apiClient.get(`/invoices/number/${invoiceNumber}`);
    return response.data;
  },
  cancel: async (id: string, data: InvoiceCancelRequest): Promise<Invoice> => {
    const response = await apiClient.put(`/invoices/${id}/cancel`, data);
    return response.data;
  },
};
