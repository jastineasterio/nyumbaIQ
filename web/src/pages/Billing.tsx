import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { billingApi } from '../api/billing';
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
import type { BillingSchedule, BillingGenerateRequest } from '../types';

const Billing = () => {
  const queryClient = useQueryClient();
  const [isGenerateOpen, setIsGenerateOpen] = useState(false);
  const [form, setForm] = useState<BillingGenerateRequest>({ propertyId: '', month: '' });
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['billing-schedules'], queryFn: () => billingApi.getSchedules(), staleTime: 30000 });
  const generateMutation = useMutation({ mutationFn: billingApi.generate, onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['billing-schedules'] }); setIsGenerateOpen(false); } });

  if (error) return <div><PageHeader title="Billing" /><ErrorState onRetry={() => refetch()} /></div>;

  const schedules = data ?? [];

  return (
    <div>
      <PageHeader title="Billing" description="Manage billing schedules" action={<Button onClick={() => setIsGenerateOpen(true)}>Generate Billing</Button>} />
      {isLoading ? <TableSkeleton rows={5} cols={4} /> : schedules.length === 0 ? (
        <EmptyState title="No billing schedules" description="Create a billing schedule to automate invoicing." action={<Button onClick={() => setIsGenerateOpen(true)}>Generate Billing</Button>} />
      ) : (
        <Card padding="none">
          <Table data={schedules} keyExtractor={(s) => s.id} columns={[
            { key: 'name', header: 'Name' },
            { key: 'propertyName', header: 'Property', render: (s) => s.propertyName ?? s.propertyId },
            { key: 'frequency', header: 'Frequency' },
            { key: 'amount', header: 'Amount', render: (s) => `$${s.amount.toLocaleString()}` },
            { key: 'dueDay', header: 'Due Day' },
            { key: 'isActive', header: 'Active', render: (s) => <Badge variant={s.isActive ? 'success' : 'secondary'}>{s.isActive ? 'Yes' : 'No'}</Badge> },
          ]} />
        </Card>
      )}
      <Modal open={isGenerateOpen} onClose={() => setIsGenerateOpen(false)} title="Generate Billing" size="lg">
        <div className="space-y-4">
          <Input label="Property ID" value={form.propertyId} onChange={(e) => setForm({ ...form, propertyId: e.target.value })} />
          <Input label="Month (YYYY-MM)" value={form.month} onChange={(e) => setForm({ ...form, month: e.target.value })} placeholder="2025-01" />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsGenerateOpen(false)}>Cancel</Button>
            <Button onClick={() => generateMutation.mutate(form)} isLoading={generateMutation.isPending}>Generate</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default Billing;
