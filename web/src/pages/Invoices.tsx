import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { invoicesApi } from '../api/invoices';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Table from '../components/ui/Table';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import Modal from '../components/ui/Modal';
import Input from '../components/ui/Input';
import ErrorState from '../components/ui/ErrorState';
import { TableSkeleton } from '../components/ui/Skeleton';
import EmptyState from '../components/ui/EmptyState';
import type { Invoice, InvoiceCancelRequest } from '../types';

const Invoices = () => {
  const queryClient = useQueryClient();
  const [isCancelOpen, setIsCancelOpen] = useState(false);
  const [selectedId, setSelectedId] = useState('');
  const [cancelReason, setCancelReason] = useState('');
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['invoices'], queryFn: () => invoicesApi.getAll(0, 100), staleTime: 30000 });
  const cancelMutation = useMutation({
    mutationFn: (data: InvoiceCancelRequest) => invoicesApi.cancel(selectedId, data),
    onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['invoices'] }); setIsCancelOpen(false); setCancelReason(''); },
  });

  if (error) return <div><PageHeader title="Invoices" /><ErrorState onRetry={() => refetch()} /></div>;

  const invoices = data?.content ?? [];

  const statusVariant = (status: string) => {
    switch (status) {
      case 'PAID': return 'success';
      case 'OVERDUE': return 'danger';
      case 'SENT': return 'info';
      case 'DRAFT': return 'secondary';
      case 'CANCELLED': return 'warning';
      default: return 'secondary';
    }
  };

  return (
    <div>
      <PageHeader title="Invoices" description="Manage invoices" />
      {isLoading ? <TableSkeleton rows={5} cols={6} /> : invoices.length === 0 ? (
        <EmptyState title="No invoices" description="Invoices will appear here once generated." />
      ) : (
        <Card padding="none">
          <Table data={invoices} keyExtractor={(i) => i.id} columns={[
            { key: 'invoiceNumber', header: 'Invoice #' },
            { key: 'tenantName', header: 'Tenant', render: (i) => i.tenantName ?? i.tenantId },
            { key: 'propertyName', header: 'Property', render: (i) => i.propertyName ?? i.propertyId },
            { key: 'dueDate', header: 'Due Date' },
            { key: 'amount', header: 'Amount', render: (i) => `$${i.amount.toLocaleString()}` },
            { key: 'balance', header: 'Balance', render: (i) => `$${i.balance.toLocaleString()}` },
            { key: 'status', header: 'Status', render: (i) => <Badge variant={statusVariant(i.status)}>{i.status}</Badge> },
            { key: 'actions', header: '', render: (i) => i.status !== 'CANCELLED' && i.status !== 'PAID' ? (
              <Button size="sm" variant="ghost" onClick={() => { setSelectedId(i.id); setIsCancelOpen(true); }}>Cancel</Button>
            ) : null },
          ]} />
        </Card>
      )}
      <Modal open={isCancelOpen} onClose={() => setIsCancelOpen(false)} title="Cancel Invoice">
        <div className="space-y-4">
          <Input label="Reason" value={cancelReason} onChange={(e) => setCancelReason(e.target.value)} />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsCancelOpen(false)}>Close</Button>
            <Button variant="danger" onClick={() => cancelMutation.mutate({ reason: cancelReason })} isLoading={cancelMutation.isPending}>Cancel Invoice</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default Invoices;
