import apiClient from './client';
import type { Expense, ExpenseFormData, PaginatedResponse } from '../types';

export const expensesApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<Expense>> => {
    const response = await apiClient.get(`/expenses?page=${page}&size=${size}`);
    return response.data;
  },
  getById: async (id: string): Promise<Expense> => {
    const response = await apiClient.get(`/expenses/${id}`);
    return response.data;
  },
  create: async (data: ExpenseFormData): Promise<Expense> => {
    const response = await apiClient.post('/expenses', data);
    return response.data;
  },
  update: async (id: string, data: Partial<ExpenseFormData>): Promise<Expense> => {
    const response = await apiClient.put(`/expenses/${id}`, data);
    return response.data;
  },
  submit: async (id: string): Promise<Expense> => {
    const response = await apiClient.post(`/expenses/${id}/submit`);
    return response.data;
  },
  approve: async (id: string): Promise<Expense> => {
    const response = await apiClient.post(`/expenses/${id}/approve`);
    return response.data;
  },
  pay: async (id: string): Promise<Expense> => {
    const response = await apiClient.post(`/expenses/${id}/pay`);
    return response.data;
  },
  cancel: async (id: string): Promise<Expense> => {
    const response = await apiClient.post(`/expenses/${id}/cancel`);
    return response.data;
  },
};
