import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { maintenanceApi } from '../api/maintenance';
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
import type { MaintenanceRequest, MaintenanceFormData, MaintenanceCostData } from '../types';

const Maintenance = () => {
  const queryClient = useQueryClient();
  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [isCostOpen, setIsCostOpen] = useState(false);
  const [selectedId, setSelectedId] = useState('');
  const [createForm, setCreateForm] = useState<MaintenanceFormData>({ propertyId: '', unitId: '', title: '', description: '', priority: 'MEDIUM' });
  const [costForm, setCostForm] = useState<MaintenanceCostData>({ description: '', amount: 0 });
  const [assigneeId, setAssigneeId] = useState('');
  const { data, isLoading, error, refetch } = useQuery({ queryKey: ['maintenance'], queryFn: () => maintenanceApi.getAll(0, 100), staleTime: 30000 });
  const createMutation = useMutation({ mutationFn: maintenanceApi.create, onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['maintenance'] }); setIsCreateOpen(false); } });
  const assignMutation = useMutation({ mutationFn: (data: { assigneeId: string }) => maintenanceApi.assign(selectedId, data), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['maintenance'] }) });
  const statusMutation = useMutation({ mutationFn: (data: { status: MaintenanceRequest['status'] }) => maintenanceApi.updateStatus(selectedId, data), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['maintenance'] }) });
  const costMutation = useMutation({ mutationFn: (data: MaintenanceCostData) => maintenanceApi.addCost(selectedId, data), onSuccess: () => { queryClient.invalidateQueries({ queryKey: ['maintenance'] }); setIsCostOpen(false); } });

  if (error) return <div><PageHeader title="Maintenance" /><ErrorState onRetry={() => refetch()} /></div>;

  const requests = data?.content ?? [];

  const statusVariant = (status: string) => {
    switch (status) {
      case 'COMPLETED': return 'success';
      case 'IN_PROGRESS': return 'info';
      case 'ASSIGNED': return 'warning';
      case 'OPEN': return 'secondary';
      case 'CANCELLED': return 'danger';
      default: return 'secondary';
    }
  };

  return (
    <div>
      <PageHeader title="Maintenance" description="Manage maintenance requests" action={<Button onClick={() => setIsCreateOpen(true)}>New Request</Button>} />
      {isLoading ? <TableSkeleton rows={5} cols={5} /> : requests.length === 0 ? (
        <EmptyState title="No requests" description="Submit a maintenance request to get started." action={<Button onClick={() => setIsCreateOpen(true)}>New Request</Button>} />
      ) : (
        <Card padding="none">
          <Table data={requests} keyExtractor={(m) => m.id} columns={[
            { key: 'title', header: 'Title' },
            { key: 'propertyName', header: 'Property', render: (m) => m.propertyName ?? m.propertyId },
            { key: 'unitName', header: 'Unit', render: (m) => m.unitName ?? m.unitId },
            { key: 'priority', header: 'Priority' },
            { key: 'status', header: 'Status', render: (m) => <Badge variant={statusVariant(m.status)}>{m.status}</Badge> },
            { key: 'actions', header: '', render: (m) => (
              <div className="flex gap-2">
                <Button size="sm" variant="secondary" onClick={() => { setSelectedId(m.id); setAssigneeId(''); }}>Assign</Button>
                <Button size="sm" onClick={() => { setSelectedId(m.id); statusMutation.mutate({ status: 'IN_PROGRESS' }); }}>Start</Button>
                <Button size="sm" onClick={() => { setSelectedId(m.id); setCostForm({ description: '', amount: 0 }); setIsCostOpen(true); }}>Add Cost</Button>
              </div>
            ) },
          ]} />
        </Card>
      )}
      <Modal open={isCreateOpen} onClose={() => setIsCreateOpen(false)} title="New Maintenance Request" size="lg">
        <div className="space-y-4">
          <Input label="Property ID" value={createForm.propertyId} onChange={(e) => setCreateForm({ ...createForm, propertyId: e.target.value })} />
          <Input label="Unit ID" value={createForm.unitId} onChange={(e) => setCreateForm({ ...createForm, unitId: e.target.value })} />
          <Input label="Title" value={createForm.title} onChange={(e) => setCreateForm({ ...createForm, title: e.target.value })} />
          <Input label="Description" value={createForm.description} onChange={(e) => setCreateForm({ ...createForm, description: e.target.value })} />
          <Select label="Priority" value={createForm.priority} onChange={(e) => setCreateForm({ ...createForm, priority: e.target.value as MaintenanceRequest['priority'] })} options={[
            { value: 'LOW', label: 'Low' }, { value: 'MEDIUM', label: 'Medium' }, { value: 'HIGH', label: 'High' }, { value: 'URGENT', label: 'Urgent' },
          ]} />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsCreateOpen(false)}>Cancel</Button>
            <Button onClick={() => createMutation.mutate(createForm)} isLoading={createMutation.isPending}>Create</Button>
          </div>
        </div>
      </Modal>
      <Modal open={!!selectedId && !isCostOpen} onClose={() => setSelectedId('')} title="Assign Maintenance">
        <div className="space-y-4">
          <Input label="Assignee ID" value={assigneeId} onChange={(e) => setAssigneeId(e.target.value)} />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setSelectedId('')}>Close</Button>
            <Button onClick={() => assignMutation.mutate({ assigneeId })} isLoading={assignMutation.isPending}>Assign</Button>
          </div>
        </div>
      </Modal>
      <Modal open={isCostOpen} onClose={() => setIsCostOpen(false)} title="Add Maintenance Cost">
        <div className="space-y-4">
          <Input label="Description" value={costForm.description} onChange={(e) => setCostForm({ ...costForm, description: e.target.value })} />
          <Input label="Amount" type="number" value={costForm.amount} onChange={(e) => setCostForm({ ...costForm, amount: Number(e.target.value) })} />
          <div className="flex justify-end gap-3">
            <Button variant="secondary" onClick={() => setIsCostOpen(false)}>Cancel</Button>
            <Button onClick={() => costMutation.mutate(costForm)} isLoading={costMutation.isPending}>Add Cost</Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default Maintenance;
