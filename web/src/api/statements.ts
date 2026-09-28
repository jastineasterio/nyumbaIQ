import apiClient from './client';
import type { Statement, StatementGenerateRequest, PaginatedResponse } from '../types';

export const statementsApi = {
  getAll: async (page = 0, size = 20): Promise<PaginatedResponse<Statement>> => {
    const response = await apiClient.get(`/statements?page=${page}&size=${size}`);
    return response.data;
  },
  getById: async (id: string): Promise<Statement> => {
    const response = await apiClient.get(`/statements/${id}`);
    return response.data;
  },
  getByNumber: async (statementNumber: string): Promise<Statement> => {
    const response = await apiClient.get(`/statements/number/${statementNumber}`);
    return response.data;
  },
  generate: async (data: StatementGenerateRequest): Promise<Statement> => {
    const response = await apiClient.post('/statements/generate', data);
    return response.data;
  },
};
