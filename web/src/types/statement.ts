export interface Statement {
  id: string;
  statementNumber: string;
  tenantId: string;
  tenantName?: string;
  propertyId: string;
  propertyName?: string;
  unitId: string;
  unitName?: string;
  periodStart: string;
  periodEnd: string;
  openingBalance: number;
  closingBalance: number;
  generatedAt: string;
}

export interface StatementGenerateRequest {
  tenantId: string;
  periodStart: string;
  periodEnd: string;
}
