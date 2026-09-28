export type DocumentType = 'ID_CARD' | 'PASSPORT' | 'DRIVERS_LICENSE' | 'PROOF_OF_ADDRESS' | 'EMPLOYMENT_LETTER' | 'BANK_STATEMENT' | 'OTHER';
export type DocumentStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface KYCProfile {
  id: string;
  tenantId: string;
  fullName: string;
  email: string;
  phone: string;
  idNumber?: string;
  address?: string;
  employmentStatus?: string;
  monthlyIncome?: number;
  status: DocumentStatus;
  documentsCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface KYCDocument {
  id: string;
  tenantId: string;
  documentType: DocumentType;
  fileName: string;
  fileSize: number;
  mimeType: string;
  status: DocumentStatus;
  rejectionReason?: string;
  uploadedAt: string;
  reviewedAt?: string;
}

export interface DocumentUploadResponse {
  id: string;
  fileName: string;
  status: DocumentStatus;
}

export interface KYCDocumentFormData {
  documentType: DocumentType;
  file: File;
}
