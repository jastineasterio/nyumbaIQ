import apiClient from './client';
import type { Receipt, PaginatedResponse, ReceiptDownloadResponse } from '../types';

export const receiptsApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<Receipt>> => {
    const response = await apiClient.get(`/receipts?page=${page}&size=${size}`);
    return response.data;
  },
  getById: async (id: string): Promise<Receipt> => {
    const response = await apiClient.get(`/receipts/${id}`);
    return response.data;
  },
  getByNumber: async (receiptNumber: string): Promise<Receipt> => {
    const response = await apiClient.get(`/receipts/number/${receiptNumber}`);
    return response.data;
  },
  download: async (id: string): Promise<ReceiptDownloadResponse> => {
    const response = await apiClient.get(`/receipts/${id}/download`);
    return response.data;
  },
};
