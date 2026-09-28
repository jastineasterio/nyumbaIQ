export interface Receipt {
  id: string;
  receiptNumber: string;
  paymentId: string;
  tenantId: string;
  tenantName?: string;
  amount: number;
  method: string;
  issuedAt: string;
}

export interface ReceiptDownloadResponse {
  url: string;
  filename: string;
}
