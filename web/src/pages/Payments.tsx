import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { paymentsApi } from '../api/payments';
import PageHeader from '../components/ui/PageHeader';
import Card from '../components/ui/Card';
import Table from '../components/ui/Table';
import Badge from '../components/ui/Badge';
import Button from '../components/ui/Button';
import Modal from '../components/ui/Modal';
import Input from '../components/ui/Input';
import Select from '../components/ui/Select';
import ErrorState from '../components/ui/ErrorState';
import { TableSkeleton } from '../components/ui/Skeleton';
import EmptyState from '../components/ui/EmptyState';
import type { Payment } from '../types';

const Payments = () => {
  const queryClient = useQueryClient();
  const [isPayOpen, setIsPayOpen] = useState(false);
  const [form, setForm] = useState({ tenantId: '', propertyId: '', unitId: '', amount: 0, method: 'MOBILE_MONEY' as Payment['method'] });
  const [reference, setReference] = useState('');
  const [isVerifyOpen, setIsVerifyOpen] = useState(false);
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['payments'], queryFn: () => paymentsApi.getAll(0, 100), staleTime: 30000 });
  const payMutation = useMutation({ mutationFn: (data: Partial<Payment>) => paymentsApi.create(data), onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['payments'] }); setIsPayOpen(false); } });
  const verifyMutation = useMutation({ mutationFn: (data: { reference: string }) => paymentsApi.verify(data), onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['payments'] }); setIsVerifyOpen(false); } });

  if (error) return <div><PageHeader title="Payments" /><ErrorState onRetry={() => refetch()} /></div>;

  const payments = data?.content ?? [];

  const statusVariant = (status: string) => {
    switch (status) {
      case 'COMPLETED': return 'success';
      case 'PENDING': return 'warning';
      case 'FAILED': return 'danger';
      case 'REFUNDED': return 'info';
      default: return 'secondary';
    }
  };

  return (
    <div>
      <PageHeader title="Payments" description="Track and manage payments" action={<Button onClick={() => setIsPayOpen(true)}>Record Payment</Button>} />
      {isLoading ? <TableSkeleton rows={5} cols={6} /> : payments.length === 0 ? (
        <EmptyState title="No payments" description="Recorded payments will appear here." action={<Button onClick={() => setIsPayOpen(true)}>Record Payment</Button>} />
      ) : (
        <Card padding="none">
          <Table data={payments} keyExtractor={(p) => p.id} columns={[
            { key: 'reference', header: 'Reference' },
            { key: 'tenantName', header: 'Tenant', render: (p) => p.tenantName ?? p.tenantId },
            { key: 'propertyName', header: 'Property', render: (p) => p.propertyName ?? p.propertyId },
            { key: 'amount', header: 'Amount', render: (p) => `$${p.amount.toLocaleString()}` },
            { key: 'method', header: 'Method' },
            { key: 'status', header: 'Status', render: (p) => <Badge variant={statusVariant(p.status)}>{p.status}</Badge> },
            { key: 'paidAt', header: 'Paid At', render: (p) => p.paidAt ? new Date(p.paidAt).toLocaleString() : '-' },
          ]} />
        </Card>
      )}
      <Modal open={isPayOpen} onClose={() => setIsPayOpen(false)} title="Record Payment" size="lg">
        <div className="space-y-4">
          <Input label="Tenant ID" value={form.tenantId} onChange={(e) => setForm({ ...form, tenantId: e.target.value })} />
          <Input label="Property ID" value={form.propertyId} onChange={(e) => setForm({ ...form, propertyId: e.target.value })} />
          <Input label="Unit ID" value={form.unitId} onChange={(e) => setForm({ ...form, unitId: e.target.value })} />
          <Input label="Amount" type="number" value={form.amount} onChange={(e) => setForm({ ...form, amount: Number(e.target.value) })} />
          <Select label="Method" value={form.method} onChange={(e) => setForm({ ...form, method: e.target.value as Payment['method'] })} options={[
            { value: 'MOBILE_MONEY', label: 'Mobile Money' },
            { value: 'BANK_TRANSFER', label: 'Bank Transfer' },
            { value: 'CASH', label: 'Cash' },
            { value: 'CHEQUE', label: 'Cheque' },
          ]} />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsPayOpen(false)}>Cancel</Button>
            <Button onClick={() => payMutation.mutate(form)} isLoading={payMutation.isPending}>Save Payment</Button>
          </div>
        </div>
      </Modal>
      <Modal open={isVerifyOpen} onClose={() => setIsVerifyOpen(false)} title="Verify Payment">
        <div className="space-y-4">
          <Input label="Reference" value={reference} onChange={(e) => setReference(e.target.value)} />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsVerifyOpen(false)}>Close</Button>
            <Button onClick={() => verifyMutation.mutate({ reference })} isLoading={verifyMutation.isPending}>Verify</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default Payments;
