export interface IncomeSummaryReport {
  period: string;
  totalIncome: number;
  collected: number;
  outstanding: number;
}

export interface ExpenseSummaryReport {
  period: string;
  totalExpenses: number;
  byCategory: { category: string; amount: number }[];
}

export interface OutstandingRentReport {
  tenantId: string;
  tenantName: string;
  propertyName: string;
  unitName: string;
  amountDue: number;
  daysOverdue: number;
}

export interface PropertyRevenueReport {
  propertyId: string;
  propertyName: string;
  totalRevenue: number;
  collectedRevenue: number;
  occupancyRate: number;
}
