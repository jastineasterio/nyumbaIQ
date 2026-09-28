import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { leasesApi } from '../api/leases';
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
import type { Lease, LeaseFormData } from '../types';

const Leases = () => {
  const queryClient = useQueryClient();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [form, setForm] = useState<LeaseFormData>({ propertyId: '', unitId: '', tenantId: '', startDate: '', endDate: '', rentAmount: 0, depositAmount: 0 });
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['leases'], queryFn: () => leasesApi.getAll(0, 100), staleTime: 30000 });
  const createMutation = useMutation({ mutationFn: leasesApi.create, onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['leases'] }); setIsModalOpen(false); setForm({ propertyId: '', unitId: '', tenantId: '', startDate: '', endDate: '', rentAmount: 0, depositAmount: 0 }); } });
  const approveMutation = useMutation({ mutationFn: (id: string) => leasesApi.approve(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['leases'] }) });
  const terminateMutation = useMutation({ mutationFn: (id: string) => leasesApi.terminate(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['leases'] }) });

  if (error) return <div><PageHeader title="Leases" description="Manage property leases" /><ErrorState onRetry={() => refetch()} /></div>;

  const leases = data?.content ?? [];

  const statusVariant = (status: string) => {
    switch (status) {
      case 'ACTIVE': return 'success';
      case 'PENDING': return 'warning';
      case 'TERMINATED': return 'danger';
      case 'EXPIRED': return 'info';
      default: return 'secondary';
    }
  };

  return (
    <div>
      <PageHeader title="Leases" description="Manage property leases" action={<Button onClick={() => setIsModalOpen(true)}>New Lease</Button>} />
      {isLoading ? <TableSkeleton rows={5} cols={6} /> : leases.length === 0 ? (
        <EmptyState title="No leases found" description="Create your first lease to get started." action={<Button onClick={() => setIsModalOpen(true)}>New Lease</Button>} />
      ) : (
        <Card padding="none">
          <Table data={leases} keyExtractor={(l) => l.id} columns={[
            { key: 'leaseNumber', header: 'Lease #', render: (l) => <Link to={`/leases/${l.id}`} className="text-primary-600 hover:underline">{l.leaseNumber}</Link> },
            { key: 'propertyName', header: 'Property', render: (l) => l.propertyName ?? l.propertyId },
            { key: 'unitName', header: 'Unit', render: (l) => l.unitName ?? l.unitId },
            { key: 'tenantName', header: 'Tenant', render: (l) => l.tenantName ?? l.tenantId },
            { key: 'endDate', header: 'End Date' },
            { key: 'rentAmount', header: 'Rent', render: (l) => `$${l.rentAmount.toLocaleString()}` },
            { key: 'status', header: 'Status', render: (l) => <Badge variant={statusVariant(l.status)}>{l.status}</Badge> },
            { key: 'actions', header: '', render: (l) => (
              <div className="flex gap-2">
                {l.status === 'PENDING' && <Button size="sm" onClick={() => approveMutation.mutate(l.id)} isLoading={approveMutation.isPending}>Approve</Button>}
                {l.status === 'ACTIVE' && <Button size="sm" variant="danger" onClick={() => terminateMutation.mutate(l.id)} isLoading={terminateMutation.isPending}>Terminate</Button>}
              </div>
            ) },
          ]} />
        </Card>
      )}
      <Modal open={isModalOpen} onClose={() => setIsModalOpen(false)} title="New Lease" size="lg">
        <div className="space-y-4">
          <Input label="Property ID" value={form.propertyId} onChange={(e) => setForm({ ...form, propertyId: e.target.value })} />
          <Input label="Unit ID" value={form.unitId} onChange={(e) => setForm({ ...form, unitId: e.target.value })} />
          <Input label="Tenant ID" value={form.tenantId} onChange={(e) => setForm({ ...form, tenantId: e.target.value })} />
          <div className="grid grid-cols-2 gap-4">
            <Input label="Start Date" type="date" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} />
            <Input label="End Date" type="date" value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <Input label="Rent Amount" type="number" value={form.rentAmount} onChange={(e) => setForm({ ...form, rentAmount: Number(e.target.value) })} />
            <Input label="Deposit Amount" type="number" value={form.depositAmount} onChange={(e) => setForm({ ...form, depositAmount: Number(e.target.value) })} />
          </div>
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsModalOpen(false)}>Cancel</Button>
            <Button onClick={() => createMutation.mutate(form)} isLoading={createMutation.isPending}>Create Lease</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default Leases;
