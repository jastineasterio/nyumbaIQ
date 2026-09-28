import { useQuery } from '@tanstack/react-query';
import { reportsApi } from '../api/reports';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import Table from '../components/ui/Table';
import ErrorState from '../components/ui/ErrorState';
import { CardSkeleton } from '../components/ui/Skeleton';
import EmptyState from '../components/ui/EmptyState';
import type { IncomeSummaryReport, ExpenseSummaryReport, OutstandingRentReport } from '../types';

const Reports = () => {
  const { data: income, isLoading: incomeLoading, error: incomeError, refetch: incomeRefetch } = useQuery({ queryKey: ['report-income'], queryFn: reportsApi.getIncomeSummary, staleTime: 60000 });
  const { data: expenses, isLoading: expensesLoading, error: expensesError, refetch: expensesRefetch } = useQuery({ queryKey: ['report-expenses'], queryFn: reportsApi.getExpenseSummary, staleTime: 60000 });
  const { data: outstanding, isLoading: outstandingLoading, error: outstandingError, refetch: outstandingRefetch } = useQuery({ queryKey: ['report-outstanding'], queryFn: reportsApi.getOutstandingRent, staleTime: 60000 });

  const renderError = (error: unknown, refetch: () => void) => error ? <ErrorState onRetry={refetch} /> : null;

  return (
    <div>
      <PageHeader title="Reports" description="Financial and operational reports" />
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <h3 className="text-lg font-semibold text-slate-900 mb-4">Income Summary</h3>
          {incomeLoading ? <CardSkeleton /> : renderError(incomeError, incomeRefetch) ?? (income ? (
            <div className="space-y-3">
              <div className="flex justify-between"><span className="text-slate-600">Period</span><span className="font-medium">{income.period}</span></div>
              <div className="flex justify-between"><span className="text-slate-600">Total Income</span><span className="font-medium">${income.totalIncome.toLocaleString()}</span></div>
              <div className="flex justify-between"><span className="text-slate-600">Collected</span><span className="font-medium text-green-600">${income.collected.toLocaleString()}</span></div>
              <div className="flex justify-between"><span className="text-slate-600">Outstanding</span><span className="font-medium text-red-600">${income.outstanding.toLocaleString()}</span></div>
            </div>
          ) : <EmptyState title="No data" />)}
        </Card>
        <Card>
          <h3 className="text-lg font-semibold text-slate-900 mb-4">Expense Summary</h3>
          {expensesLoading ? <CardSkeleton /> : renderError(expensesError, expensesRefetch) ?? (expenses ? (
            <div className="space-y-3">
              <div className="flex justify-between"><span className="text-slate-600">Period</span><span className="font-medium">{expenses.period}</span></div>
              <div className="flex justify-between"><span className="text-slate-600">Total Expenses</span><span className="font-medium text-red-600">${expenses.totalExpenses.toLocaleString()}</span></div>
              {expenses.byCategory.map((c) => (
                <div key={c.category} className="flex justify-between"><span className="text-slate-600">{c.category}</span><span className="font-medium">${c.amount.toLocaleString()}</span></div>
              ))}
            </div>
          ) : <EmptyState title="No data" />)}
        </Card>
        <Card className="lg:col-span-2">
          <h3 className="text-lg font-semibold text-slate-900 mb-4">Outstanding Rent</h3>
          {outstandingLoading ? <CardSkeleton /> : renderError(outstandingError, outstandingRefetch) ?? (outstanding && outstanding.length > 0 ? (
            <Table data={outstanding} keyExtractor={(o) => o.tenantId} columns={[
              { key: 'tenantName', header: 'Tenant' },
              { key: 'propertyName', header: 'Property' },
              { key: 'unitName', header: 'Unit' },
              { key: 'amountDue', header: 'Amount Due', render: (o) => `$${o.amountDue.toLocaleString()}` },
              { key: 'daysOverdue', header: 'Days Overdue', render: (o) => <Badge variant={o.daysOverdue > 30 ? 'danger' : 'warning'}>{o.daysOverdue}</Badge> },
            ]} />
          ) : <EmptyState title="No outstanding rent" />)}
        </Card>
      </div>
    </div>
  );
};

export default Reports;
