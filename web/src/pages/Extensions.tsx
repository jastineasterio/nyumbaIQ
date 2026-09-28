import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { extensionsApi } from '../api/extensions';
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
import type { Extension, ExtensionFormData } from '../types';

const Extensions = () => {
  const queryClient = useQueryClient();
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [form, setForm] = useState<ExtensionFormData>({ leaseId: '', currentEndDate: '', requestedEndDate: '', reason: '' });
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['extensions'], queryFn: () => extensionsApi.getAll(0, 100), staleTime: 30000 });
  const createMutation = useMutation({ mutationFn: extensionsApi.create, onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['extensions'] }); setIsCreateOpen(false); } });
  const approveMutation = useMutation({ mutationFn: (id: string) => extensionsApi.approve(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['extensions'] }) });
  const rejectMutation = useMutation({ mutationFn: (id: string) => extensionsApi.reject(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['extensions'] }) });
  const cancelMutation = useMutation({ mutationFn: (id: string) => extensionsApi.cancel(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['extensions'] }) });

  if (error) return <div><PageHeader title="Extensions" /><ErrorState onRetry={() => refetch()} /></div>;

  const extensions = data?.content ?? [];

  const statusVariant = (status: string) => {
    switch (status) {
      case 'APPROVED': return 'success';
      case 'PENDING': return 'warning';
      case 'REJECTED': return 'danger';
      case 'CANCELLED': return 'secondary';
      default: return 'secondary';
    }
  };

  return (
    <div>
      <PageHeader title="Extensions" description="Lease extension requests" action={<Button onClick={() => setIsCreateOpen(true)}>New Extension</Button>} />
      {isLoading ? <TableSkeleton rows={5} cols={5} /> : extensions.length === 0 ? (
        <EmptyState title="No extensions" description="Request a lease extension to continue tenancy." action={<Button onClick={() => setIsCreateOpen(true)}>New Extension</Button>} />
      ) : (
        <Card padding="none">
          <Table data={extensions} keyExtractor={(e) => e.id} columns={[
            { key: 'leaseNumber', header: 'Lease #', render: (e) => e.leaseNumber ?? e.leaseId },
            { key: 'tenantName', header: 'Tenant', render: (e) => e.tenantName ?? e.tenantId },
            { key: 'currentEndDate', header: 'Current End' },
            { key: 'requestedEndDate', header: 'Requested End' },
            { key: 'status', header: 'Status', render: (e) => <Badge variant={statusVariant(e.status)}>{e.status}</Badge> },
            { key: 'actions', header: '', render: (e) => (
              <div className="flex gap-2">
                {e.status === 'PENDING' && <><Button size="sm" onClick={() => approveMutation.mutate(e.id)} isLoading={approveMutation.isPending}>Approve</Button><Button size="sm" variant="danger" onClick={() => rejectMutation.mutate(e.id)} isLoading={rejectMutation.isPending}>Reject</Button></>}
                {e.status === 'PENDING' && <Button size="sm" variant="ghost" onClick={() => cancelMutation.mutate(e.id)}>Cancel</Button>}
              </div>
            ) },
          ]} />
        </Card>
      )}
      <Modal open={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="Request Extension" size="lg">
        <div className="space-y-4">
          <Input label="Lease ID" value={form.leaseId} onChange={(e) => setForm({ ...form, leaseId: e.target.value })} />
          <div className="grid grid-cols-2 gap-4">
            <Input label="Current End Date" type="date" value={form.currentEndDate} onChange={(e) => setForm({ ...form, currentEndDate: e.target.value })} />
            <Input label="Requested End Date" type="date" value={form.requestedEndDate} onChange={(e) => setForm({ ...form, requestedEndDate: e.target.value })} />
          </div>
          <Input label="Reason" value={form.reason} onChange={(e) => setForm({ ...form, reason: e.target.value })} />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsCreateOpen(false)}>Cancel</Button>
            <Button onClick={() => createMutation.mutate(form)} isLoading={createMutation.isPending}>Submit</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default Extensions;
