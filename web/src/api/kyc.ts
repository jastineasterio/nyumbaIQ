import apiClient from './client';
import type {
  KYCProfile,
  KYCDocument,
  PaginatedResponse,
  DocumentType,
} from '../types';

export const kycApi = {
  getProfile: async (tenantId: string): Promise<KYCProfile> => {
    const response = await apiClient.get(`/kyc/tenant/${tenantId}/profile`);
    return response.data;
  },

  uploadDocument: async (
    tenantId: string,
    data: { documentType: DocumentType; file: File }
  ): Promise<{ id: string; fileName: string; status: string }> => {
    const formData = new FormData();
    formData.append('documentType', data.documentType);
    formData.append('file', data.file);
    const response = await apiClient.post(`/kyc/tenant/${tenantId}/documents`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },

  getDocuments: async (tenantId: string, page = 0, size = 20): Promise<PaginatedResponse<KYCDocument>> => {
    const response = await apiClient.get(`/kyc/tenant/${tenantId}/documents?page=${page}&size=${size}`);
    return response.data;
  },

  getDocument: async (id: string): Promise<KYCDocument> => {
    const response = await apiClient.get(`/kyc/documents/${id}`);
    return response.data;
  },

  reviewDocument: async (id: string, status: 'APPROVED' | 'REJECTED', reason?: string): Promise<KYCDocument> => {
    const response = await apiClient.put(`/kyc/documents/${id}/review`, { status, rejectionReason: reason });
    return response.data;
  },

  deleteDocument: async (id: string): Promise<void> => {
    await apiClient.delete(`/kyc/documents/${id}`);
  },

  downloadDocument: async (id: string): Promise<Blob> => {
    const response = await apiClient.get(`/kyc/documents/${id}/download`, { responseType: 'blob' });
    return response.data;
  },
};
