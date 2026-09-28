import apiClient from './client';
import type { IncomeSummaryReport, ExpenseSummaryReport, OutstandingRentReport, PropertyRevenueReport } from '../types';

export const reportsApi = {
  getIncomeSummary: async (): Promise<IncomeSummaryReport> => {
    const response = await apiClient.get('/reports/income/summary');
    return response.data;
  },
  getExpenseSummary: async (): Promise<ExpenseSummaryReport> => {
    const response = await apiClient.get('/reports/expense/summary');
    return response.data;
  },
  getOutstandingRent: async (): Promise<OutstandingRentReport[]> => {
    const response = await apiClient.get('/reports/outstanding-rent');
    return response.data;
  },
  getPropertyRevenue: async (propertyId: string): Promise<PropertyRevenueReport> => {
    const response = await apiClient.get(`/reports/property/${propertyId}/revenue`);
    return response.data;
  },
};
